package de.hda.rts.car;

import static de.hda.rts.car.Task.car;
import static de.hda.rts.car.Task.street;

public class Fahren extends Task
{
	public Fahren(long t, int V, int dx,int dy)
	{
		super(t);
		car.V = V;
		car.Dx = dx;
		car.Dy = dy;
	}

	@Override
	protected void execute()
	{
		double length = Math.sqrt(car.Dx*car.Dx+ car.Dy*car.Dy);		
		double realV = (this.period * length) / 1000.0;
		
		car.x=+ car.Dx*realV;
		car.y=+ car.Dy*realV;
				
		double entf = street.distance(car.x, car.y);
		if(entf < 0)
		{
			
		}
	}

}
