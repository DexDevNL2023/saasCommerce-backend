package io.dexproject.achatservice.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message){
    	super(message);
    }
}

