package de.hda.rts.simulation;

import java.util.Comparator;
import java.util.List;

public class DeadlineMonotonicScheduling extends Scheduling {

	private final Comparator<Task> deadlinePriorityComparator = new Comparator<Task>() {
		@Override
		public int compare(Task left, Task right) {
			return left.getInfo().getDeadline() - right.getInfo().getDeadline();
		}
	};

	public DeadlineMonotonicScheduling(List<TaskInfo> taskInfos) {
		super(taskInfos);
	}

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return deadlinePriorityComparator;
	}
//
//	@Override
//	protected Task getNext(Task currentTask, List<Task> waitingTasks) {
//		Task nextTask = waitingTasks.get(0);
//
//		if (currentTask != null && !currentTask.isFinished()) {
//			if (getPriorityComparator().compare(currentTask, nextTask) < 0) {
//				return currentTask;
//			}
//		}
//
//		return nextTask;
//	}

	@Override
	public String toString() {
		return "DMS";
	}
}
