package de.hda.rts.car;

public class Main {

	public static void main(String[] args) {
		Display d = new Display(2000);
		d.start();
		
		try {
			d.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
