package io.dexproject.achatservice.generic.security.crud.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
	private String emailOrPhone;
	@NotBlank(message = "Le nom est obligatoire")
    private String lastName;
	private String firstName;
	private String email;
    private String phone;
	private String adresse;
    private boolean usingQr = false;
	private String langKey;
	@Size(max = 256, message = "La taille de l'image doit être inférieur ou égale à 256")
	private String imageUrl;
	private List<Long> roles;

	public SignupRequest(String lastName, String firstName, String email, String phone, String langKey, String imageUrl, List<Long> roles) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.langKey = langKey;
		this.imageUrl = imageUrl;
		this.email = email;
		this.phone = phone;
		this.adresse = null;
		this.usingQr = false;
		this.roles = roles;
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
		private List<Long> roles;

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

		public Builder addRole(final List<Long> roles) {
			this.roles = roles;
			return this;
		}

		public SignupRequest build() {
			return new SignupRequest(lastName, firstName, email, phone, langKey, imageUrl, roles);
		}
	}
}