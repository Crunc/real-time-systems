package de.hda.rts.car;

public class Handbrake extends Task {

	public Handbrake(long t) {
		super(t);
	}

	@Override
	protected void execute() {
		if (isToFarAway()) {
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
		return false;
	}
}
