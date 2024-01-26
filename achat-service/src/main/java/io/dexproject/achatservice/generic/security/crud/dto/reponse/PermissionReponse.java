package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionReponse extends BaseReponse {
    private DroitReponse droit;
    private Boolean hasPermission;
}
