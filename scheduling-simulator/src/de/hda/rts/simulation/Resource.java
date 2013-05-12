package de.hda.rts.simulation;

import com.google.common.base.Preconditions;

public class Resource {
	public static final Resource NO_RESOURCE = Resource.builder().info(ResourceInfo.NO_RESOURCE).build();
	
	private ResourceInfo info;
	
	public ResourceInfo getInfo() {
		return info;
	}
	
	public String getName() {
		return getInfo().getName();
	}

	public int getPriority() {
		return getInfo().getPriority();
	}
	
	public static Resource.Builder builder() {
		return new Resource.Builder();
	}
	
	public static class Builder {
		private Resource res;
		
		public Builder() {
			res = new Resource();
		}
		
		public Builder info(ResourceInfo info) {
			Preconditions.checkArgument(info != null, "info must not be null");
			
			res.info = info;
			return this;
		}
		
		public Resource build() {
			Preconditions.checkState(res.info != null, "info must be set");
			
			return res;
		}
	}
}
