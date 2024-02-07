package io.dexproject.achatservice.security.crud.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Veillez renseignez votre login svp!")
    private String emailOrPhone;
	private String passwordTxt;
    private Boolean generatePassword = false;
}