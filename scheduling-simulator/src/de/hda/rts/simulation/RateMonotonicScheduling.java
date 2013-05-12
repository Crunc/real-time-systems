package de.hda.rts.simulation;

import java.util.Comparator;

public class RateMonotonicScheduling extends StaticScheduling {

	@Override
	protected Comparator<Task> getPriorityComparator() {
		return new Comparator<Task>() {

			@Override
			public int compare(Task left, Task right) {
				int result = left.getPeriod() -  right.getPeriod();
				
				if (result == 0) {
					return -1;
				}
				
				return result;
			}
		};
	}
	
	@Override
	public String toString() {
		return "RMS";
	}
}
