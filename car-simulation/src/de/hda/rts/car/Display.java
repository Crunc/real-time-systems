package de.hda.rts.car;

import de.hda.rts.simulation.util.Log;

public class Display extends Task {

	private static final String TAG = Display.class.getSimpleName();

	public Display(long t) {
		super(t);
	}

	@Override
	protected void execute() {
		Log.d(TAG, "pos: {0}, {1}", car.x, car.y);
	}
}
