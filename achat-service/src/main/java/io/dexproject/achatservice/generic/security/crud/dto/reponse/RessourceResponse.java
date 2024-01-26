package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.Value;

@Value
public class RessourceResponse {
    private Boolean success;
	private String message;
    private Object objectValue;

	public RessourceResponse(String message) {
	    this.success = true;
	    this.message = message;
	    this.objectValue = null;
	}

	public RessourceResponse(Boolean success, String message) {
	    this.success = success;
	    this.message = message;
	    this.objectValue = null;
	}

	public RessourceResponse(String message, Object objectValue) {
	    this.success = true;
	    this.message = message;
	    this.objectValue = objectValue;
	}

	public RessourceResponse(Boolean success, Object objectValue) {
        this.success = success;
        this.message = "Une erreur est survenue pendant le traitement de votre requête.";
        this.objectValue = objectValue;
    }
}