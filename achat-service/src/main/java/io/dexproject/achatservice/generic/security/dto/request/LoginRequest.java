package io.dexproject.achatservice.generic.security.dto.request;

import lombok.Value;

@Value
public class LoginRequest {
	private String email;
	private String passwordTxt;
}