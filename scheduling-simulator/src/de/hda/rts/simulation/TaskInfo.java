package de.hda.rts.simulation;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class TaskInfo {
	
	public static final int NO_PRIORITY = 0;
	
	private String name;
	private List<String> execution;
	private int period;
	private int deadline;
	
	private int priority = NO_PRIORITY;
	private int releaseTime = 0;

	public String getName() {
		return name;
	}
	
	public List<String> getExecution() {
		return execution;
	}

	public int getComputationTime() {
		return execution.size();
	}

	public int getPeriod() {
		return period;
	}

	public int getDeadline() {
		return deadline;
	}

	public int getPriority() {
		return priority;
	}
	
	public int getReleaseTime() {
		return releaseTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TaskInfo other = (TaskInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(TaskInfo.class)
				.add("name", getName())
				.add("execution", Joiner.on("").join(execution))
				.add("period", getPeriod())
				.add("deadline", getDeadline())
				.add("priority", getPriority() == NO_PRIORITY ? "none" : getPriority())
				.add("releaseTime", getReleaseTime())
				.toString();
	}

	public static TaskInfo.Builder builder() {
		return new TaskInfo.Builder();
	}
	
	public static class Builder {
		
		private TaskInfo info;
		
		private Builder() {
			info = new TaskInfo();
		}
		
		public Builder name(String name) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name must not be null nor empty");
			
			info.name = name;
			return this;
		}
		
		public Builder period(int period) {
			Preconditions.checkArgument(period > 0, "period must be greater than 0");
			
			info.period = period;
			return this;
		}
		
		public Builder period(String value) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value must not be null nor empty");
			
			return period(Integer.parseInt(value));
		}
		
		public Builder deadline(int deadline) {
			Preconditions.checkArgument(deadline > 0, "deadline must be greater than 0");
			
			info.deadline = deadline;
			return this;
		}
		
		public Builder deadline(String value) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value must not be null nor empty");
			
			return deadline(Integer.parseInt(value));
		}
		
		public Builder priority(int priority) {
			Preconditions.checkArgument(priority > 0 || priority == NO_PRIORITY, "priority must be greater than 0 or equal to NO_PRIORITY");
			
			info.priority = priority;
			return this;
		}
		
		public Builder priority(String value) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value must not be null nor empty");
			
			return priority(Integer.parseInt(value));
		}
		
		public Builder releaseTime(int releaseTime) {
			Preconditions.checkArgument(releaseTime >= 0, "releaseTime must be greater than or equal to 0");
			
			info.releaseTime = releaseTime;
			return this;
		}
		
		public Builder releaseTime(String value) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value must not be null nor empty");
			
			return releaseTime(Integer.parseInt(value));
		}
		
		public Builder execution(String value) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "value must not be null nor empty");
			
			String[] resNames = value.trim().split("");
			List<String> execution = Lists.newArrayListWithCapacity(resNames.length);
			
			for (String name: resNames) {
				if (!Strings.isNullOrEmpty(name)) {
					execution.add(name);
				}
			}
			
			return execution(execution);
		}
		
		public Builder execution(List<String> execution) {
			Preconditions.checkArgument(execution != null, "execution must not be null");
			Preconditions.checkArgument(execution.size() > 0, "execution must not be empty");
			
			info.execution = execution;
			return this;
		}

		public TaskInfo build() {
			Preconditions.checkState(info.name != null, "name must be set");
			Preconditions.checkState(info.execution != null && info.execution.size() > 0, "execution must be set");
			Preconditions.checkState(info.period > 0, "period must be set");
			Preconditions.checkState(info.priority > 0 || info.priority == NO_PRIORITY, "priority must be set");
			
			if (info.deadline <= 0) {
				info.deadline = info.period;
			}
			
			return info;
		}
	}
}
