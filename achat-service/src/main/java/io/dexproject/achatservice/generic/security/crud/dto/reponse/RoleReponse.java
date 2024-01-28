package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import io.dexproject.achatservice.generic.security.crud.entities.Role;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleReponse extends BaseReponse {
    private RoleName libelle;
    private Boolean isSuper;
    private Boolean isGrant;
    private List<PermissionReponse> permissions;

    public RoleReponse(Role role) {
        this.setId(role.getId());
        this.libelle = role.getLibelle();
        this.isSuper = role.getIsSuper();
        this.isGrant = role.getIsGrant();
    }
}
