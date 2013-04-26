package de.hda.rts.simulation;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Scheduling {

	private final List<Task> tasks;
	private final List<Task> waitingTasks;
	private Task computingTask;
	private int stepCount;
	private final SchedulingReport report;

	protected Scheduling(List<TaskInfo> taskInfos) {
		tasks = new ArrayList<Task>(taskInfos.size());

		for (TaskInfo taskInfo : taskInfos) {
			tasks.add(new Task(taskInfo));
		}

		stepCount = 0;
		waitingTasks = new ArrayList<Task>(tasks);
		report = new SchedulingReport(toString(), tasks);
	}

	public void makeSchedule(int treshhold) {
		Collections.sort(tasks, getPriorityComparator());
		Collections.sort(waitingTasks, getPriorityComparator());

		if (stepCount == 0) {
			boolean error = false;
			while (!error && !isFinished() && stepCount < treshhold) {
				++stepCount;
				error = !updateTasks();
				report.addStep(computingTask);
			}

			if (error) {
				printError(System.out);
			}
		}
	}

	private boolean updateTasks() {
		computingTask = getNext(computingTask, waitingTasks);
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
				task.update(stepCount);

				if (task.isWaiting()) {
					waitingTasks.add(task);
				}
			}
		}

		Collections.sort(waitingTasks, getPriorityComparator());
		return true;
	}

	private boolean isFinished() {
//		if (stepCount == 0 || stepCount == 1) return false;
		
		boolean finished = true;

		for (Task task : tasks) {
			finished &= task.isFinished();
//			if (stepCount > 0) {
				finished &= stepCount % task.getInfo().getPeriod() == 0;
//			}
		}

		return finished;
	}

	private void printError(PrintStream out) {
		System.out.println("[ERROR]: "
				+ computingTask.getInfo().getName()
				+ " exceeded deadline of "
				+ computingTask.getInfo().getDeadline() + " in step "
				+ stepCount);
	}
	
	public void printReport(PrintStream out) {
		out.println(report.toString());
	}

	protected Task getNext(Task currentTask, List<Task> waitingTasks) {
		Task nextTask = waitingTasks.size() > 0 ? waitingTasks.get(0) : null;
		
		if (currentTask != null && !currentTask.isFinished()) {
			if (nextTask == null || getPriorityComparator().compare(currentTask, nextTask) < 0) {
				return currentTask;
			}
		}
		
		return nextTask;
	}
	
	protected abstract Comparator<Task> getPriorityComparator();
}
