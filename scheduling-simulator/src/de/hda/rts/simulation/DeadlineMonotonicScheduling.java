package de.hda.rts.simulation;

import java.util.Comparator;

import de.hda.rts.simulation.util.Tasks;

public class DeadlineMonotonicScheduling extends Scheduling {

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return Tasks.DEADLINE_COMPARATOR;
	}

	@Override
	public String toString() {
		return "DMS";
	}
}
