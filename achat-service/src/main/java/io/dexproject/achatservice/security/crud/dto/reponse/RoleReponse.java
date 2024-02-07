package io.dexproject.achatservice.security.crud.dto.reponse;

import io.dexproject.achatservice.generic.dto.reponse.BaseReponse;
import io.dexproject.achatservice.security.crud.enums.RoleName;
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
    private List<PermissionReponse> permissions;
}
