package de.hda.rts.simulation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.hda.rts.simulation.data.ScheduleModel;
import de.hda.rts.simulation.util.Log;
import de.hda.rts.simulation.util.Resources;
import de.hda.rts.simulation.util.SchedulingComparator;

public abstract class Scheduling {

	private static final String TAG = Scheduling.class.getSimpleName();

	private TaskConfig config;

	/**
	 * Holds all available tasks.
	 */
	private List<Task> tasks;

	/**
	 * Holds all tasks that are currently waiting for execution.
	 */
	private List<Task> waitingTasks;

	/**
	 * Holds all tasks that are currently available for execution.
	 */
	private List<Task> availableTasks;
	
	/**
	 * Holds all tasks that are currently blocked by other tasks holding their
	 * required resources.
	 */
	private List<Task> blockedTasks;

	/**
	 * Allows easy access of tasks by their name.
	 */
	private Map<String, Task> taskMap;

	/**
	 * Holds all available resources.
	 */
	private Set<Resource> resources;

	/**
	 * Allows easy access of resources by their name.
	 */
	private Map<String, Resource> resMap;

	/**
	 * Keeps track of which resource is acquired by which task.
	 */
	private Map<Resource, Task> resourceMap;

	private Task computingTask;
	private int stepCount;
	private ScheduleModel model;
	private String analyzation;
	
	private Comparator<Task> taskComparator = new Comparator<Task>() {
		@Override
		public int compare(Task left, Task right) {
			int leftPrio = getPriority(left);
			int rightPrio = getPriority(right);
			
			return rightPrio - leftPrio;
		}
	};

	public void initialize(TaskConfig cfg) {
		Preconditions.checkArgument(cfg != null, "cfg must not be null");

		config = cfg;
		stepCount = 0;

		// ------------------------------------------------------
		// initialize resources
		// ------------------------------------------------------
		resources = Sets.newTreeSet(Resources.NAME_COMPARATOR);
		resMap = Maps.newHashMap();
		resourceMap = Maps.newTreeMap(Resources.NAME_COMPARATOR);
		for (ResourceInfo info : config.getResourceInfos()) {
			Resource res = Resource.builder().info(info).build();
			resources.add(res);
			resMap.put(res.getName(), res);
			resourceMap.put(res, null);
		}

		// ------------------------------------------------------
		// initialize tasks
		// ------------------------------------------------------

		tasks = Lists.newArrayList();
		for (TaskInfo info : config.getTaskInfos()) {
			Task task = Task.builder().info(info).build();
			tasks.add(task);
		}
		
		waitingTasks = Lists.newArrayListWithCapacity(tasks.size());
		availableTasks = Lists.newArrayListWithCapacity(tasks.size());
		blockedTasks = Lists.newArrayListWithCapacity(tasks.size());
		taskMap = Maps.newHashMapWithExpectedSize(tasks.size());
		
		for (Task task : tasks) {
			if (task.isReleased()) {
				waitingTasks.add(task);
				availableTasks.add(task);
			}
			taskMap.put(task.getName(), task);
		}
		
		updatePriorities();

		analyzation = analyzeStatically();
		model = new ScheduleModel(toString(), tasks);

		printResponseTimes();
	}

	protected void printResponseTimes() {
		for (Task task: getTasks()) {
			int rta = rta(task);

			Log.d(TAG, "RTA({0}): {1,number,integer}", task.getName(), rta);
		}
		Log.d(TAG, "--------------------------------------------");
	}

	public boolean doNextStep(int treshhold) {
		if (!isFinished() && getStep() < treshhold) {
			++stepCount;
			boolean error = !updateTasks();
			return error;
		}

		return false;
	}

	private boolean updateTasks() {
		updatePriorities();
		computingTask = getNextTask(getAvailableTasks());
		
		waitingTasks.clear();
		availableTasks.clear();
		blockedTasks.clear();

		for (Task task : tasks) {
			if (computingTask != null) {
				if (task == computingTask) {
					Resource resource = getResource(task.getNextExecutionStep());

					if (resource == Resource.NO_RESOURCE || acquire(task, resource)) {
						task.compute();
					}

					Resource nextResource = getResource(task.getNextExecutionStep());
					if (resource == Resource.NO_RESOURCE || nextResource != resource) {
						release(task, resource);
					}
				}
			}

			if (task.isDeadlineExceeded(stepCount)) {
				Log.e(TAG, "Task {0} exceeded deadline of {1} in step {2}", task.getName(), task.getDeadline(), getStep());
				return false;
			}
		}

		if (!isFinished()) {
			for (Task task : tasks) {
				task.update(getStep());

				if (task.isWaiting()) {
					waitingTasks.add(task);
					
					Resource resource = getResource(task.getNextExecutionStep());
					if (isAvailable(task, resource)) {
						availableTasks.add(task);
					}
					else {
						blockedTasks.add(task);
					}
				}
			}
		}
		
		for (Task task: tasks) {
			if (task == computingTask) {
				model.addStep(ScheduleModel.StepType.EXECUTING, task, getResource(task.getLastExecutionStep()));
			}
			else if (availableTasks.contains(task)) {
				model.addStep(ScheduleModel.StepType.WAITING, task, null);
			}
			else if (blockedTasks.contains(task)) {
				model.addStep(ScheduleModel.StepType.BLOCKED, task, getResource(task.getNextExecutionStep()));
			}
			else if (!task.isReleased()) { 
				model.addStep(ScheduleModel.StepType.NOT_RELEASED, task, null);
			}
			else {
				model.addStep(ScheduleModel.StepType.DONE, task, null);
			}
		}
		model.finishStep();

		return true;
	}

	private boolean isFinished() {
		boolean finished = true;

		for (Task task : tasks) {
			finished &= task.isFinished();
			finished &= getStep() % task.getInfo().getPeriod() == 0;
		}

		return finished;
	}

	Task getHolder(Resource resource) {
		if (resource == Resource.NO_RESOURCE) {
			return null;
		}

		return resourceMap.get(resource);
	}

	boolean isAvailable(Task task, Resource resource) {
		Task holder = getHolder(resource);
		return holder == null || holder == task;
	}

	boolean acquire(Task task, Resource resource) {
		if (isAvailable(task, resource)) {
			resourceMap.put(resource, task);
			return true;
		}

		return false;
	}

	boolean release(Task task, Resource resource) {
		if (getHolder(resource) == task) {
			resourceMap.put(resource, null);
			return true;
		}

		return false;
	}

	protected String analyzeStatically() {
		return "";
	}

	/**
	 * Base implementation that chooses the waiting {@link Task} with the
	 * highest priority (static).
	 */
	protected Task getNextTask(List<Task> availableTasks) {
		if (availableTasks.isEmpty()) {
			return null;
		}
		else {
			return availableTasks.get(0);
		}
	}
	
	void updatePriorities() {
		calculatePriorities();
		
		Collections.sort(getTasks(), taskComparator);
		Collections.sort(getWaitingTasks(), taskComparator);
		Collections.sort(getAvailableTasks(), taskComparator);
		Collections.sort(getBlockedTasks(), taskComparator);
	}
	
	protected abstract void calculatePriorities();
	protected abstract int getPriority(Task task);

	protected int rta(Task task) {
		int c = task.getComputationTime();
		
		int taskIdx = getTasks().indexOf(task);

		if (taskIdx == 0) {
			return c;
		}
		else {
			int rOld = c;
			int r = -1;

			while (r != rOld && r <= task.getDeadline()) {
				rOld = r;
				r = c;

				for (int idx = taskIdx - 1; idx >= 0; --idx) {
					Task other = getTasks().get(idx);
					int ta = other.getPeriod();
					int ca = other.getComputationTime();

					int s = (int) Math.ceil((double) rOld / ta);

					r += s * ca;
				}
			}

			return r;
		}
	}

	protected Task getComputingTask() {
		return computingTask;
	}

	protected List<Task> getWaitingTasks() {
		return waitingTasks;
	}
	
	protected List<Task> getAvailableTasks() {
		return availableTasks;
	}
	
	protected List<Task> getBlockedTasks() {
		return blockedTasks;
	}

	protected int getStep() {
		return stepCount;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public Task getTask(String name) {
		return taskMap.get(name);
	}
	
	public Set<Resource> getResources() {
		return resources;
	}

	public Resource getResource(String name) {
		if (config.getResource(name) == ResourceInfo.NO_RESOURCE) {
			return Resource.NO_RESOURCE;
		}

		return resMap.get(name);
	}

	public ScheduleModel getModel() {
		return model;
	}

	public String getAnalyzation() {
		return analyzation;
	}
	
	protected final SchedulingComparator<Task> nameComparator = new SchedulingComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			return left.getName().compareTo(right.getName());
		}
	};
	
	protected final Comparator<Task> periodComparator = new SchedulingComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			int result = left.getPeriod() - right.getPeriod();
			
			if (result == 0) {
				return -1;
			}
			
			return result;
		}
	};
	
	public final Comparator<Task> deadlineComparator = new SchedulingComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			int result = left.getDeadline() - right.getDeadline();
			
			if (result == 0) {
				return -1;
			}
			
			return result;
		}
	};
	
	public final Comparator<Task> priorityComparator = new SchedulingComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			int result = right.getPriority() - left.getPriority();
			
			if (result == 0) {
				return -1;
			}
			
			return result;
		}
	};
}
