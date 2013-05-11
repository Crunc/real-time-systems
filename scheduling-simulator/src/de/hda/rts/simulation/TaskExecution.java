package de.hda.rts.simulation;

import java.util.ArrayList;

import com.google.common.base.Preconditions;

import de.hda.rts.simulation.util.Log;

public class TaskExecution extends ArrayList<ResourceInfo> {
	
	private static final long serialVersionUID = 5269937334961182382L;
	
	private static final String TAG = TaskExecution.class.getSimpleName();
	
	private static final String RESOURCE_NAME_DELIM = "";

	public static TaskExecution parse(String value, TaskConfig config) {
		Preconditions.checkArgument(value != null, "value must not be null");
		Preconditions.checkArgument(config != null, "config must not be null");
		
		// split something like "EEQVE" into ["E", "E", "Q", "V", "E"]
		String[] resourceNames = value.trim().split(RESOURCE_NAME_DELIM);
		
		TaskExecution.Builder builder = TaskExecution.builder();
		
		for (String name: resourceNames) {
			ResourceInfo resource = config.getResource(name);
			
			if (resource != null) {
				builder.resource(resource);
			}
			else {
				Log.e(TAG, "No resource found for name {0}", name);
			}
		}
		
		return builder.build();
	}

	
	public static TaskExecution.Builder builder() {
		return new TaskExecution.Builder();
	}
	
	public static class Builder {
		private TaskExecution execution;
		
		private Builder() {
			execution = new TaskExecution();
		}
		
		public Builder resource(ResourceInfo resource) {
			Preconditions.checkArgument(resource != null, "resource must not be null");
			
			execution.add(resource);
			return this;
		}
		
		public TaskExecution build() {
			return execution;
		}
	}
}
