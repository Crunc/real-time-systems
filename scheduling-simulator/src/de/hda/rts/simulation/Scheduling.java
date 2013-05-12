package de.hda.rts.simulation;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.hda.rts.simulation.data.ScheduleModel;
import de.hda.rts.simulation.util.Log;
import de.hda.rts.simulation.util.Resources;

public abstract class Scheduling {

	private static final String TAG = Scheduling.class.getSimpleName();

	private TaskConfig config;

	/**
	 * Holds all available tasks.
	 */
	private NavigableSet<Task> tasks;

	/**
	 * Holds all tasks that are currently waiting for execution.
	 */
	private NavigableSet<Task> waitingTasks;

	/**
	 * Holds all tasks that are currently available for execution.
	 */
	private NavigableSet<Task> availableTasks;
	
	/**
	 * Holds all tasks that are currently blocked by other tasks holding their
	 * required resources.
	 */
	private NavigableSet<Task> blockedTasks;

	/**
	 * Allows easy access of tasks by their name.
	 */
	private Map<String, Task> taskMap;

	/**
	 * Holds all available resources.
	 */
	private List<Resource> resources;

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

	public void initialize(TaskConfig cfg) {
		Preconditions.checkArgument(cfg != null, "cfg must not be null");

		config = cfg;

		// ------------------------------------------------------
		// initialize tasks
		// ------------------------------------------------------
		tasks = Sets.newTreeSet(getPriorityComparator());
		waitingTasks = Sets.newTreeSet(getPriorityComparator());
		availableTasks = Sets.newTreeSet(getPriorityComparator());
		blockedTasks = Sets.newTreeSet(getPriorityComparator());
		taskMap = Maps.newHashMapWithExpectedSize(tasks.size());
		for (TaskInfo info : config.getTaskInfos()) {
			Task task = Task.builder().info(info).build();
			tasks.add(task);
			waitingTasks.add(task);
			availableTasks.add(task);
			taskMap.put(task.getName(), task);
		}

		// ------------------------------------------------------
		// initialize resources
		// ------------------------------------------------------
		resources = Lists.newArrayList();
		resMap = Maps.newHashMap();
		resourceMap = Maps.newTreeMap(Resources.NAME_COMPARATOR);
		for (ResourceInfo info : config.getResourceInfos()) {
			Resource res = Resource.builder().info(info).build();
			resources.add(res);
			resMap.put(res.getName(), res);
			resourceMap.put(res, null);
		}

		stepCount = 0;

		analyzation = analyzeStatically();
		model = new ScheduleModel(toString(), tasks);

		printResponseTimes();
	}

	protected void printResponseTimes() {
		List<Task> taskList = Lists.newArrayList(tasks);
		for (int idx = 0; idx < taskList.size(); ++idx) {
			int rta = calculateResponseTime(idx, taskList);

			Log.d(TAG, "RTA({0}): {1,number,integer}", taskList.get(idx).getName(), rta);
		}
		Log.d(TAG, "--------------------------------------------");
	}

	public boolean doNextStep(int treshhold) {
		if (!isFinished() && getStep() < treshhold) {
			++stepCount;
			boolean error = !updateTasks();

			if (error) {
				printError(System.out);
			}

			return error;
		}

		return false;
	}

	private boolean updateTasks() {
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
					if (resource == Resource.NO_RESOURCE && nextResource != resource) {
						release(task, resource);
					}
				}
			}

			if (task.isDeadlineExceeded(stepCount)) {
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
				model.addStep(ScheduleModel.StepType.EXEC, task, getResource(task.getLastExecutionStep()));
			}
			else if (availableTasks.contains(task)) {
				model.addStep(ScheduleModel.StepType.WAIT, task, getResource(task.getNextExecutionStep()));
			}
			else if (blockedTasks.contains(task)) {
				model.addStep(ScheduleModel.StepType.BLOCK, task, getResource(task.getNextExecutionStep()));
			}
			else {
				model.addStep(ScheduleModel.StepType.DONE, task, getResource(task.getNextExecutionStep()));
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

	private void printError(PrintStream out) {
		String errorMessage = new StringBuilder("[ERROR]: ")
				.append(getComputingTask().getInfo().getName())
				.append(" exceeded deadline of ")
				.append(getComputingTask().getInfo().getDeadline())
				.append(" in step ")
				.append(getStep()).toString();

		out.println(errorMessage);
	}

	public void printReport(PrintStream out) {
		out.println(model.toString());
	}

	protected String analyzeStatically() {
		return "";
	}

	/**
	 * Base implementation that chooses the waiting {@link Task} with the
	 * highest priority (static).
	 */
	protected Task getNextTask(NavigableSet<Task> availableTasks) {
		if (availableTasks.isEmpty()) {
			return null;
		}
		else {
			return availableTasks.first();
		}
	}

	protected abstract Comparator<Task> getPriorityComparator();

	protected int calculateResponseTime(int taskIdx, List<Task> tasks) {
		Task task = tasks.get(taskIdx);
		int c = task.getInfo().getComputationTime();

		if (taskIdx == 0) {
			return c;
		}
		else {
			int rOld = c;
			int r = -1;

			while (r != rOld && r <= task.getInfo().getDeadline()) {
				rOld = r;
				r = c;

				for (int idx = taskIdx - 1; idx >= 0; --idx) {
					TaskInfo info = tasks.get(idx).getInfo();
					int ta = info.getPeriod();
					int ca = info.getComputationTime();

					int s = (int) Math.ceil((double) rOld / ta);

					r += s * ca;
				}
			}

			return r;
		}
	}

	public int rta(Task task) {
		// get a set that contains only the tasks with a higher priority than
		// the given task
		NavigableSet<Task> tasks = getTasks().headSet(task, false);

		int c = task.getComputationTime();

		if (tasks.isEmpty()) {
			return c;
		}
		else {
			int rOld = c;
			int r = -1;

			while (r != rOld && r <= task.getDeadline()) {
				rOld = r;
				r = c;

				// iterate over the tasks from lowest to highest priority
				Iterator<Task> iter = tasks.descendingIterator();
				while (iter.hasNext()) {
					Task t = iter.next();
					int ta = t.getPeriod();
					int ca = t.getComputationTime();

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

	protected NavigableSet<Task> getWaitingTasks() {
		return waitingTasks;
	}
	
	protected NavigableSet<Task> getAvailableTasks() {
		return availableTasks;
	}
	
	protected NavigableSet<Task> getBlockedTasks() {
		return blockedTasks;
	}

	protected int getStep() {
		return stepCount;
	}

	public NavigableSet<Task> getTasks() {
		return tasks;
	}

	public Task getTask(String name) {
		return taskMap.get(name);
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
}
