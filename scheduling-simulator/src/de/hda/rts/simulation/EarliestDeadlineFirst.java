package de.hda.rts.simulation;

import java.util.Comparator;
import java.util.List;

import de.hda.rts.simulation.util.Tasks;

public class EarliestDeadlineFirst extends Scheduling {

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return Tasks.NAME_COMPARATOR;
	}
	
	@Override
	protected String analyzeStatically() {
		boolean analyzable = true;
		double schedulability = 0.0;
		
		for(Task task: getTasks()) {
			double c = task.getInfo().getComputationTime();
			double t = task.getInfo().getPeriod();
			double d = task.getInfo().getDeadline();
			
			schedulability += c / t;
			analyzable &= d - t < 0.001;
		}
		
		if (analyzable) {
			return String.format("Schedule possible: %.2f <= 1.0", schedulability);
		}
		else {
			return "Not analyzable";
		}
	}

	@Override
	protected Task getNextTask() {
		List<Task> waitingTasks = getWaitingTasks();

		Task next = null;
		int nextDist = Integer.MAX_VALUE;
		
		for (Task task: waitingTasks) {
			int dist = getDeadlineDistance(task);
			if (dist < nextDist) {
				next = task;
				nextDist = dist;
			}
		}

		return next;
	}

	int getDeadlineDistance(Task task) {
		int t = task.getInfo().getPeriod();
		int d = task.getInfo().getDeadline();
		return d - (getStep() % t);
	}
	
	@Override
	public String toString() {
		return "EDF";
	}
}
