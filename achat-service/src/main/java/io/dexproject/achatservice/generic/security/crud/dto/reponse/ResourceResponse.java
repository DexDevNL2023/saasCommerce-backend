package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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

    public ResourceResponse(Boolean success, Object objectValue) {
        this.success = success;
        this.message = "Une erreur est survenue pendant le traitement de votre requête.";
        this.objectValue = objectValue;
    }
}