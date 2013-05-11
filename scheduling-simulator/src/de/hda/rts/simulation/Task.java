package de.hda.rts.simulation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Task {
	private TaskInfo info;
	private int computed = 0;
	
	public int getComputed() {
		return computed;
	}
	
	public TaskInfo getInfo() {
		return info;
	}
	
	public String getName() {
		return getInfo().getName();
	}

	public int getComputationTime() {
		return getInfo().getComputationTime();
	}

	public int getPeriod() {
		return getInfo().getPeriod();
	}

	public int getDeadline() {
		return getInfo().getDeadline();
	}

	public int getPriority() {
		return getInfo().getPriority();
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Task)) {
			return false;
		}
		Task other = (Task) obj;
		if (info == null) {
			if (other.info != null) {
				return false;
			}
		} else if (!info.equals(other.info)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(Task.class)
				.add("info", getInfo())
				.toString();
	}
	
	public static Task.Builder builder() {
		return new Task.Builder();
	}
	
	public static class Builder {
		private final Task task;
		
		private Builder() {
			task = new Task();
		}
		
		public Builder info(TaskInfo info) {
			Preconditions.checkArgument(info != null, "info must not be null");
			
			task.info = info;
			return this;
		}
		
		public Task build() {
			Preconditions.checkState(task.info != null, "info must be set");
			
			return task;
		}
	}
}
