package de.hda.rts.simulation;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Task {
	private TaskInfo info;
	private int computed = 0;
	private boolean released = false;
	
	public int getComputed() {
		return computed;
	}
	
	public TaskInfo getInfo() {
		return info;
	}
	
	public String getName() {
		return getInfo().getName();
	}
	
	public List<String> getExecution() {
		return getInfo().getExecution();
	}
	
	public String getLastExecutionStep() {
		if (computed > 0 && computed <= getExecution().size()) {
			return getExecution().get(computed - 1);
		}
		
		return null;
	}
	
	public String getNextExecutionStep() {
		if (computed < getExecution().size()) {
			return getExecution().get(computed);
		}
		
		return null;
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
	
	public int getReleaseTime() {
		return getInfo().getReleaseTime();
	}
	
	public boolean isReleased() {
		return getReleaseTime() == 0 || released;
	}
	
	public boolean isFinished() {
		return computed >= getExecution().size();
	}
	
	public boolean isWaiting() {
		return isReleased() && computed < getExecution().size();
	}
	
	public void compute() {
		++computed;
	}
	
	public boolean isDeadlineExceeded(int step) {
		return !isFinished() && step % info.getDeadline() == 0;
	}
	
	public void update(int step) {
		released = step >= getReleaseTime();
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
				.add("name", getName())
				.add("execution", Joiner.on("").join(getExecution()))
				.add("period", getPeriod())
				.add("deadline", getDeadline())
				.add("priority", getPriority() == TaskInfo.NO_PRIORITY ? "none" : getPriority())
				.add("releaseTime", getReleaseTime())
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

	public boolean checkRelease(int stepCount) {
		released = stepCount >= getReleaseTime();
		return released;
	}
}
