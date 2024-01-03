package io.dexproject.achatservice.security.dto.reponse;

import lombok.Value;

@Value
public class JwtAuthenticationResponse {
	private String accessToken;
	private boolean authenticated;
	private LoginReponse user;
}