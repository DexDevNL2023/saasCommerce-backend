package io.dexproject.achatservice.generic.entity;

import lombok.Value;

@Value
public class ResourceResponse {
    private Boolean success;
	private String message;
    private Object objectValue;

	public ResourceResponse(String message) {
	    this.success = true;
	    this.message = message;
	    this.objectValue = null;
	}

	public ResourceResponse(Boolean success, String message) {
	    this.success = success;
	    this.message = message;
	    this.objectValue = null;
	}

	public ResourceResponse(String message, Object objectValue) {
	    this.success = true;
	    this.message = message;
	    this.objectValue = objectValue;
	}
}