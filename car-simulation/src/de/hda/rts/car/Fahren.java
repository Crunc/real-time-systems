package de.hda.rts.car;

public class Fahren extends Task
{
	public Fahren(long t, int V, int dx,int dy)
	{
		super(t);
		Task.car.V = V;
		Task.car.Dx = dx;
		Task.car.Dy = dy;
	}

	@Override
	protected void execute()
	{
		double realV = (this.period * Task.car.V) / 1000.0;
	}

}
