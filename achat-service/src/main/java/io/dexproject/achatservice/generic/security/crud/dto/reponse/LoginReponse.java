package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginReponse extends BaseReponse {
	private String displayName;
	private String lastName;
	private String firstName;
	private String accesToken;
	private String langKey;
	private String imageUrl;
	private RoleName role;

	public String getDisplayName() {
		return this.displayName = !this.firstName.isEmpty() ? this.lastName + " " + this.firstName : this.lastName;
	}
}
