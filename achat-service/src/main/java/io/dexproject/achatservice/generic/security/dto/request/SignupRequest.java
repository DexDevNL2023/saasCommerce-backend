package io.dexproject.achatservice.generic.security.dto.request;

import io.dexproject.achatservice.generic.enums.RoleName;
import io.dexproject.achatservice.generic.validators.PasswordMatches;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
@PasswordMatches
public class SignupRequest {
	private String displayName;
	@NotEmpty
	private String lastName;
	@NotEmpty
	private String firstName;
	@NotEmpty
    private String email;
	private String passwordTxt;
	private String matchingPassword;
    private String phone;
    private String adresse;
	private boolean usingQr;
	private RoleName role;

	public SignupRequest(String lastName, String firstName, String email, RoleName role) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.displayName = !this.firstName.isEmpty() ? this.lastName + " " + this.firstName : this.lastName;
		this.email = email;
		this.passwordTxt = "";
		this.matchingPassword = "";
		this.phone = null;
		this.adresse = null;
		this.usingQr = false;
		this.role = role == null ? RoleName.CUSTOMER : role;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String lastName;
		private String firstName;
		private String email;
		private RoleName role;

		public Builder addLastName(final String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder addFirstName(final String displayName) {
			this.firstName = firstName;
			return this;
		}

		public Builder addEmail(final String email) {
			this.email = email;
			return this;
		}

		public Builder addRole(final RoleName role) {
			this.role = role;
			return this;
		}

		public SignupRequest build() {
			return new SignupRequest(lastName, firstName, email, role);
		}
	}
}