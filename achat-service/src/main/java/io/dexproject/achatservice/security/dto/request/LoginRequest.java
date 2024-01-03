package io.dexproject.achatservice.security.dto.request;

import lombok.Value;

@Value
public class LoginRequest {
	private String email;
	private String passwordTxt;
}