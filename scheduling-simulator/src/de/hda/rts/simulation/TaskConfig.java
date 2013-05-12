package de.hda.rts.simulation;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class TaskConfig {

	private static final String TAG = TaskConfig.class.getSimpleName();
	
	private String noResource;
	
	/**
	 * Maps resource-names to {@link ResourceInfo}s
	 */
	private Map<String, ResourceInfo> resourceInfos = Maps.newHashMap();
	
	/**
	 * Maps task-names to {@link TaskInfo}s
	 */
	private Map<String, TaskInfo> taskInfos = Maps.newHashMap();

	protected TaskConfig() {
		
	}
	
	public ResourceInfo getResource(String name) {
		if (noResource != null && noResource.equals(name)) {
			return ResourceInfo.NO_RESOURCE;
		}
		
		return resourceInfos.get(name);
	}
	
	public TaskInfo getTaskInfo(String name) {
		return taskInfos.get(name);
	}

	public Iterable<TaskInfo> getTaskInfos() {
		return taskInfos.values();
	}
	
	public Iterable<ResourceInfo> getResourceInfos() {
		return resourceInfos.values();
	}
	
	public static TaskConfig.Builder builder() {
		return new TaskConfig.Builder();
	}
	
	public static class Builder {
		private TaskConfig config;
		
		private Builder() {
			config = new TaskConfig();
		}

		public Builder noResource(String noResource) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(noResource), "noResource must not be null nor empty");
			
			config.noResource = noResource;
			return this;
		}
		
		public Builder resourceInfo(ResourceInfo info) {
			Preconditions.checkArgument(info != null, "info must not be null");
			
			config.resourceInfos.put(info.getName(), info);
			return this;
		}
		
		public Builder taskInfo(TaskInfo info) {
			Preconditions.checkArgument(info != null, "info must not be null");
			
			config.taskInfos.put(info.getName(), info);
			return this;
		}
		
		public TaskConfig build() {
			Preconditions.checkState(!Strings.isNullOrEmpty(config.noResource), "noResource must be set");
			
			return config;
		}
	}
}
