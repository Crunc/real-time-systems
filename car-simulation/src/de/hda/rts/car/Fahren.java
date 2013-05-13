package de.hda.rts.car;

public class Fahren extends Task
{
	private int V = 0;
	private int d = 0;
	public Fahren(long t, int V, int d)
	{
		super(t);
		this.V = V;
		this.d = d;
	}

	@Override
	protected void execute()
	{
		double realV = (this.period * V) / 1000.0;
	}

}
