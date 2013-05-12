package de.hda.rts.simulation;


public class DeadlineMonotonicScheduling extends Scheduling {

	@Override
	protected int getPriority(Task task) {
		return 0;
	}

	@Override
	protected void calculatePriorities() {

	}
	
	@Override
	public String toString() {
		return "DMS";
	}
}
