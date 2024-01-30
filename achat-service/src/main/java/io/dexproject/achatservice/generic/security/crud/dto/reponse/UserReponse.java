package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import io.dexproject.achatservice.generic.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReponse extends BaseReponse {
    private String displayName;
    private String lastName;
    private String firstName;
    private String email;
    private String phone;
    private String adresse;
    private String langKey;
    private String imageUrl;
    private RoleName role;

    public String getDisplayName() {
        return this.displayName = !this.firstName.isEmpty() ? this.lastName + " " + this.firstName : this.lastName;
    }
}
