package de.hda.rts.car;

import de.hda.rts.simulation.util.Log;

public class Handbrake extends Task {

	private static final String TAG = Handbrake.class.getSimpleName();

	public Handbrake(long t) {
		super(t);
	}

	@Override
	protected void execute() {
		if (isToFarAway()) {
			Log.d(TAG, "BREAK!");
			
			car.Dx /= 2;
			car.Dy /= 2;
			
			if (car.Dx < 0.1) {
				car.Dx = 0;
			}
			
			if (car.Dy < 0.1) {
				car.Dy = 0;
			}
		}
	}

	private boolean isToFarAway() {
		return street.distance(car.x, car.y) > 5;
	}
}
