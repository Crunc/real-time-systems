package de.hda.rts.simulation;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;

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
	 * Allows easy access of tasks by their name.
	 */
	private Map<String, Task> taskMap;
	
	/**
	 * Holds all available resources.
	 */
	private List<Resource> resources;
	
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
		taskMap = Maps.newHashMapWithExpectedSize(tasks.size());
		for (TaskInfo info : config.getTaskInfos()) {
			Task task = Task.builder().info(info).build();
			tasks.add(task);
			waitingTasks.add(task);
			taskMap.put(task.getName(), task);
		}
		
		// ------------------------------------------------------
		// initialize resources
		// ------------------------------------------------------
		resources = Lists.newArrayList();
		resourceMap = Maps.newTreeMap(Resources.NAME_COMPARATOR);
		for (ResourceInfo info: config.getResourceInfos()) {
			Resource res = Resource.builder().info(info).build();
			resources.add(res);
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
//		if (stepCount == 0) {
//			// initialization
//			Collections.sort(tasks, getPriorityComparator());
//			Collections.sort(waitingTasks, getPriorityComparator());
//			Collections.sort(model.getTasks(), getPriorityComparator());
//		}
		
		if (!isFinished() && getStep() < treshhold) {
			++stepCount;
			boolean error = !updateTasks();
			model.addStep(computingTask, Resource.NO_RESOURCE);
			
			if (error) {
				printError(System.out);
			}
			
			return error;
		}
		
		return false;
	}

	private boolean updateTasks() {
		computingTask = getNextTask();
		waitingTasks.clear();

		for (Task task : tasks) {
			if (computingTask != null) {
				if (task == computingTask) {
					task.compute();
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
				}
			}
		}

//		Collections.sort(waitingTasks, getPriorityComparator());
//		Collections.sort(model.getTasks(), getPriorityComparator());
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

	private void printError(PrintStream out) {
		String errorMessage = new StringBuilder("[ERROR]: ")
			.append(getComputingTask().getInfo().getName())
			.append(" exceeded deadline of " )
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
	protected Task getNextTask() {
		SortedSet<Task> waitingTasks = getWaitingTasks();
		Task computingTask = getComputingTask();
		
		Task nextTask = waitingTasks.size() > 0 ? waitingTasks.first() : null;

		if (computingTask != null && !computingTask.isFinished()) {
			if (nextTask == null || getPriorityComparator().compare(computingTask, nextTask) < 0) {
				return computingTask;
			}
		}

		return nextTask;
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
					TaskInfo info =  tasks.get(idx).getInfo();
					int ta = info.getPeriod();
					int ca = info.getComputationTime();
					
					int s = (int) Math.ceil((double) rOld / ta);
					
					r += s*ca;
				}
			}
			
			return r;
		}		
	}
	
	public int rta(Task task) {
		// get a set that contains only the tasks with a higher priority than the given task
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
					
					r += s*ca;
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
	
	protected int getStep() {
		return stepCount;
	}

	public NavigableSet<Task> getTasks() {
		return tasks;
	}
	
	public Task getTask(String name) {
		return taskMap.get(name);
	}

	public ScheduleModel getModel() {
		return model;
	}
	
	public String getAnalyzation() {
		return analyzation;
	}
}
