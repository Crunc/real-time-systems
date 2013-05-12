package de.hda.rts.simulation.util;

import java.util.Comparator;

import de.hda.rts.simulation.Resource;

public final class Resources {

	private Resources() {
		throw new UnsupportedOperationException(Resources.class.getName() + " can not be initialized");
	}
	
	public static Comparator<Resource> NAME_COMPARATOR = new NullSafeComparator<Resource>() {
		@Override
		protected int safeCompare(Resource left, Resource right) {
			return left.getName().compareTo(right.getName());
		}
	};
}
