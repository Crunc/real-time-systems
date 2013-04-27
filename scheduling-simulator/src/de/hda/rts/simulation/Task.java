package de.hda.rts.simulation;

public class Task {
	private final TaskInfo info;
	private int computed = 0;
	
	public Task(TaskInfo taskInfo) {
		info = taskInfo;
	}
	
	public int getComputed() {
		return computed;
	}
	
	public TaskInfo getInfo() {
		return info;
	}
	
	public boolean isFinished() {
		return computed >= info.getComputationTime();
	}
	
	public boolean isWaiting() {
		return computed < info.getComputationTime();
	}
	
	public void compute() {
		++computed;
	}
	
	public boolean isDeadlineExceeded(int step) {
		return !isFinished() && step % info.getDeadline() == 0;
	}
	
	public void update(int step) {
		if (step % info.getPeriod() == 0) {
			computed = 0;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder(info.getName())
			.append(" C:").append(computed).append("/").append(info.getComputationTime())
			.append(" P:").append(info.getPeriod())
			.append(" D:").append(info.getDeadline())
			.toString();
	}
}
