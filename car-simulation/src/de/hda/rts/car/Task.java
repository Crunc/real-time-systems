package de.hda.rts.car;

public abstract class Task implements Runnable {

	protected int t;
	
	public Task(int t) {
		this.t = t;
	}
	
	void waitForNextCycle() {
	
	}

	public static void timer_main(Runnable task) {
		
	}
	
	public static void execute_main(Runnable task) {
		
	}
}
