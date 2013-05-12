package de.hda.rts.simulation.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import de.hda.rts.simulation.Task;

public final class Tasks {

	private Tasks() {
		// should not be instantiated
	}
	
	public static final Comparator<Task> NAME_COMPARATOR = new NullSafeComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			return left.getName().compareTo(right.getName());
		}
	};
	
	public static final Comparator<Task> PERIOD_COMPARATOR = new NullSafeComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			int result = left.getPeriod() - right.getPeriod();
			
			if (result == 0) {
				return -1;
			}
			
			return result;
		}
	};
	
	public static final Comparator<Task> DEADLINE_COMPARATOR = new NullSafeComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			int result = left.getDeadline() - right.getDeadline();
			
			if (result == 0) {
				return -1;
			}
			
			return result;
		}
	};
	
	public static final Comparator<Task> PRIORITY_COMPARATOR = new NullSafeComparator<Task>() {
		@Override
		public int safeCompare(Task left, Task right) {
			int result = right.getPriority() - left.getPriority();
			
			if (result == 0) {
				return -1;
			}
			
			return result;
		}
	};
	
	public static <T> Map<Task, T> newTaskMap() {
		return new TreeMap<Task, T>(Tasks.NAME_COMPARATOR);
	}
}
