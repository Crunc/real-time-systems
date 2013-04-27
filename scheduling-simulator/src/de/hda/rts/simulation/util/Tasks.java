package de.hda.rts.simulation.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import de.hda.rts.simulation.Task;

public final class Tasks {

	private Tasks() {
	}
	
	public static final Comparator<Task> PERIOD_COMPARATOR = new Comparator<Task>() {
		@Override
		public int compare(Task left, Task right) {
			return left.getInfo().getPeriod() - right.getInfo().getPeriod();
		}
	};
	
	public static final Comparator<Task> DEADLINE_COMPARATOR = new Comparator<Task>() {
		@Override
		public int compare(Task left, Task right) {
			return left.getInfo().getDeadline() - right.getInfo().getDeadline();
		}
	};
	
	public static final Comparator<Task> NAME_COMPARATOR = new Comparator<Task>() {
		@Override
		public int compare(Task left, Task right) {
			return left.getInfo().getName().compareTo(right.getInfo().getName());
		}
	};
	
	public static <T> Map<Task, T> newTaskMap() {
		return new TreeMap<Task, T>(Tasks.NAME_COMPARATOR);
	}
}
