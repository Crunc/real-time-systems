package de.hda.rts.simulation;

import java.util.Comparator;
import java.util.List;

public class RateMonotonicScheduling extends Scheduling {

	private final Comparator<Task> periodPriorityComparator = new Comparator<Task>() {
		@Override
		public int compare(Task left, Task right) {
			return left.getInfo().getPeriod() - right.getInfo().getPeriod();
		}
	};
	
	protected RateMonotonicScheduling(List<TaskInfo> tasks) {
		super(tasks);
	}

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return periodPriorityComparator;
	}
	
//	@Override
//	protected Task getNext(Task currentTask, List<Task> waitingTasks) {
//		Task nextTask = waitingTasks.size() > 0 ? waitingTasks.get(0) : null;
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
		return "RMS";
	}

	
}
