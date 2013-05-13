package de.hda.rts.car;

public abstract class Task extends Thread {

	protected static Car car = new Car();
	
	protected long startTime = 0L;
	
	protected final long period;
	protected final long deadline;
	
	protected long time = 0L;
	
	public Task(long t) {
		period = t;
		deadline = period;
	}
	
	long waitForNextCycle() {
		boolean sleeping = true;
		
		long result = period;
		
		while (sleeping) {	
			if (checkPeriod()) {
				sleeping = false;
			}
			
			if (sleeping && checkDeadline()) {
				result = System.currentTimeMillis() - startTime;
//				throw new RuntimeException("Deadline exceeded");
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	private boolean checkPeriod() {
		long time = System.currentTimeMillis();
		long diff = time - startTime;
		
		if (diff > period) {
			return true;
		}
		
		return false;
	}

	private boolean checkDeadline() {
		long time = System.currentTimeMillis();
		long diff = time - startTime;
		
		if (diff > deadline) {
			return true;
		}
		
		return false;
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		
		while (true) {
			
			synchronized(car) {
				execute();
			}
			
			startTime += waitForNextCycle();
		}
	}
	
	protected void sem_wait() {
		
	}

	protected abstract void execute();
}
