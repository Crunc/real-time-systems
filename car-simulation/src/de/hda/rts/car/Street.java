package de.hda.rts.car;

public class Street {

	public double bx = 0;
	public double by = 0;
	
	public double ax = 0;
	public double ay = 0;
	
	public double distance(double x, double y) {
	    double normalLength = Math.sqrt((bx-ax)*(bx-ax)+(by-ay)*(by-ay));
	    return Math.abs((x-ax)*(by-ay)-(y-ay)*(bx-ax))/normalLength;
	}
}
