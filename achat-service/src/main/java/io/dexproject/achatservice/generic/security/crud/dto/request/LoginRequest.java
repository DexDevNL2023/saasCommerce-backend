package io.dexproject.achatservice.generic.security.crud.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginRequest {
    @NotBlank(message = "Veillez renseignez votre login svp!")
    private String emailOrPhone;
	private String passwordTxt;
    private Boolean generatePassword = false;
}