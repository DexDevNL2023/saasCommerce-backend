package io.dexproject.achatservice.generic.security.crud.dto.request;

import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.validators.EnumValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class SignupRequest {
	private String emailOrPhone;
	@NotBlank(message = "Le nom est obligatoire")
	private String lastName;
	@NotEmpty
	private String firstName;
	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "le format de l'email est incorrecte", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    private String phone;
    private String adresse;
	private boolean usingQr;
	private String langKey;
	@Size(max = 256, message = "La taille de l'image doit être inférieur ou égale à 256")
	private String imageUrl;
	@EnumValidator(enumClass = RoleName.class)
	private RoleName role;

	public SignupRequest(String lastName, String firstName, String email, String phone, String langKey, String imageUrl, RoleName role) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.langKey = langKey;
		this.imageUrl = imageUrl;
		this.email = email;
		this.phone = phone;
		this.adresse = null;
		this.usingQr = false;
		this.role = role == null ? RoleName.CUSTOMER : role;
		// on construit e;ailOrPhone
		this.emailOrPhone = this.email.isEmpty() ? this.phone : this.email;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String lastName;
		private String firstName;
		private String email;
		private String phone;
		private String langKey;
		private String imageUrl;
		private RoleName role;

		public Builder addLastName(final String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder addFirstName(final String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder addEmail(final String email) {
			this.email = email;
			return this;
		}

		public Builder addPhone(final String phone) {
			this.phone = phone;
			return this;
		}

		public Builder addLangKey(final String langKey) {
			this.langKey = langKey;
			return this;
		}

		public Builder addImageUrl(final String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public Builder addRole(final RoleName role) {
			this.role = role;
			return this;
		}

		public SignupRequest build() {
			return new SignupRequest(lastName, firstName, email, phone, langKey, imageUrl, role);
		}
	}
}