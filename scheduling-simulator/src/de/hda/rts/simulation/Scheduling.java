package de.hda.rts.simulation;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hda.rts.simulation.data.ScheduleModel;

public abstract class Scheduling {

	private final List<Task> tasks;
	private final List<Task> waitingTasks;
	private Task computingTask;
	private int stepCount;
	private final ScheduleModel model;

	private final Comparator<Task> priorityComparator;
	private final String analyzation;

	protected Scheduling(List<TaskInfo> taskInfos) {
		tasks = new ArrayList<Task>(taskInfos.size());

		for (TaskInfo taskInfo : taskInfos) {
			tasks.add(new Task(taskInfo));
		}

		stepCount = 0;
		waitingTasks = new ArrayList<Task>(tasks);
		model = new ScheduleModel(toString(), tasks);
		priorityComparator = getPriorityComparator();
		analyzation = analyzeStatically();
		
		Collections.sort(tasks, priorityComparator);
		Collections.sort(waitingTasks, priorityComparator);
	}

	public boolean doNextStep(int treshhold) {
		if (!isFinished() && getStep() < treshhold) {
			++stepCount;
			boolean error = !updateTasks();
			model.addStep(computingTask);
			
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

		Collections.sort(waitingTasks, priorityComparator);
		return true;
	}

	private boolean isFinished() {
		// if (stepCount == 0 || stepCount == 1) return false;

		boolean finished = true;

		for (Task task : tasks) {
			finished &= task.isFinished();
			// if (stepCount > 0) {
			finished &= getStep() % task.getInfo().getPeriod() == 0;
			// }
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
		List<Task> waitingTasks = getWaitingTasks();
		Task computingTask = getComputingTask();
		
		Task nextTask = waitingTasks.size() > 0 ? waitingTasks.get(0) : null;

		if (computingTask != null && !computingTask.isFinished()) {
			if (nextTask == null || priorityComparator.compare(computingTask, nextTask) < 0) {
				return computingTask;
			}
		}

		return nextTask;
	}

	protected abstract Comparator<Task> getPriorityComparator();
	
	protected Task getComputingTask() {
		return computingTask;
	}

	protected List<Task> getWaitingTasks() {
		return waitingTasks;
	}
	
	protected int getStep() {
		return stepCount;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public ScheduleModel getModel() {
		return model;
	}
	
	public String getAnalyzation() {
		return analyzation;
	}
}
