package de.hda.rts.simulation.util;

import java.util.Comparator;

public abstract class SchedulingComparator<T> implements Comparator<T> {
	@Override
	public int compare(T left, T right) {
		if (left == null) {
			if (right == null) {
				// left == null && right == null
				return compareNullToNull(); 
			}
			else {
				// left == null && right != null
				return compareRightToNull(right); 
			}
		}
		else {
			if (right == null) {
				// left != null && right == null
				return compareLeftToNull(left);
			}
			else {
				// left != null && right != null
				return safeCompare(left, right);
			}
		}
	}
	
	public void refresh(int step) {
		
	}
	
	protected int compareNullToNull() {
		return 0;
	}
	
	protected int compareLeftToNull(T left) {
		return -1;
	}
	
	protected int compareRightToNull(T right) {
		return 1;
	}
	
	abstract protected int safeCompare(T left, T right);
}
