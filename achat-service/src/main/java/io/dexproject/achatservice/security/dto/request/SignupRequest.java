package io.dexproject.achatservice.security.dto.request;

import io.dexproject.achatservice.generic.validators.PasswordMatches;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
@PasswordMatches
public class SignupRequest {
	@NotEmpty
	private String displayName;
	@NotEmpty
    private String email;
	private String passwordTxt;
	private String matchingPassword;
    private String phone;
    private String adresse;
	private boolean usingQr;
	private RoleName role;

	public SignupRequest(String displayName, String email) {
		this.displayName = displayName;
		this.email = email;
		this.passwordTxt = "";
		this.matchingPassword = "";
		this.phone = null;
		this.adresse = null;
		this.usingQr = false;
		this.role = RoleName.CUSTOMER;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String displayName;
		private String email;

		public Builder addDisplayName(final String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder addEmail(final String email) {
			this.email = email;
			return this;
		}

		public SignupRequest build() {
			return new SignupRequest(displayName, email);
		}
	}
}