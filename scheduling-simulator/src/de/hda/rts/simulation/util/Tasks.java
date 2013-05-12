package de.hda.rts.simulation.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import de.hda.rts.simulation.Task;

public final class Tasks {

	public static final Comparator<Task> NAME_COMPARATOR = new Comparator<Task>() {
		@Override
		public int compare(Task left, Task right) {
			return left.getName().compareTo(right.getName());
		}
	};

	private Tasks() {
		// should not be instantiated
	}
	
	public static <T> Map<Task, T> newTaskMap() {
		return new TreeMap<Task, T>(NAME_COMPARATOR);
	}
}
