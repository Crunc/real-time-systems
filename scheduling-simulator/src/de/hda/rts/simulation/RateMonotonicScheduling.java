package de.hda.rts.simulation;

import java.util.Comparator;

import de.hda.rts.simulation.util.Tasks;

public class RateMonotonicScheduling extends Scheduling {

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return Tasks.PERIOD_COMPARATOR;
	}
	
	@Override
	public String toString() {
		return "RMS";
	}
}
