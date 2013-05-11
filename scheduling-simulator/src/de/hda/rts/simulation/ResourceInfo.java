package de.hda.rts.simulation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class ResourceInfo {
	
	public static final ResourceInfo NO_RESOURCE = ResourceInfo.builder()
			.name("E")
			.priority(0)
			.build();
	
	private String name;
	private int priority;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ResourceInfo)) {
			return false;
		}
		ResourceInfo other = (ResourceInfo) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(ResourceInfo.class)
				.add("name", getName())
				.add("priority", getPriority())
				.toString();
	}
	
	public static ResourceInfo.Builder builder() {
		return new ResourceInfo.Builder();
	}
	
	public static class Builder {
		private final ResourceInfo res;
		
		private Builder() {
			res = new ResourceInfo();
		}
		
		public Builder name(String name) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name must not be null nor empty");
			
			res.name = name;
			return this;
		}
		
		public Builder priority(int priority) {
			Preconditions.checkArgument(priority >= 0, "priority must be a positive number");
			
			res.priority = priority;
			return this;
		}
		
		public ResourceInfo build() {
			Preconditions.checkState(!Strings.isNullOrEmpty(res.name), "name must be set");
			Preconditions.checkState(res.priority >= 0, "priority must be set");
			
			return res;
		}
	}
}
