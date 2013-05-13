package de.hda.rts.car;

public class Main {

	public static void main(String[] args) {
		Display d = new Display(2000);
		d.start();
		
		Fahren f = new Fahren(100, 45, 9, 9);
		f.start();
		
		Handbrake hb = new Handbrake(120);
		hb.start();
		
		try {
			d.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
