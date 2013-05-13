package de.hda.rts.car;

public class Handbrake extends Task {

	public Handbrake(long t) {
		super(t);
	}

	@Override
	protected void execute() {
		if (isToFarAway()) {
			--position.V;
		}
	}

	private boolean isToFarAway() {
		return false;
	}
}
