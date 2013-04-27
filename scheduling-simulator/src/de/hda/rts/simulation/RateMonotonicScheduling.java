package de.hda.rts.simulation;

import java.util.Comparator;
import java.util.List;

import de.hda.rts.simulation.util.Tasks;

public class RateMonotonicScheduling extends Scheduling {

	public RateMonotonicScheduling(List<TaskInfo> tasks) {
		super(tasks);
	}

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return Tasks.PERIOD_COMPARATOR;
	}
	
	@Override
	public String toString() {
		return "RMS";
	}
}
