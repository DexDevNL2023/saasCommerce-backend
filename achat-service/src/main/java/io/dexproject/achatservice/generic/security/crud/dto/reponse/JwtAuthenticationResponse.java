package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.Value;

@Value
public class JwtAuthenticationResponse {
	private String accessToken;
	private boolean authenticated;
	private LoginReponse user;
}