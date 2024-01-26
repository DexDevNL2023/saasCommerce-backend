package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionReponse extends BaseReponse {
    private DroitReponse droit;
    private Boolean hasPermission;
}
