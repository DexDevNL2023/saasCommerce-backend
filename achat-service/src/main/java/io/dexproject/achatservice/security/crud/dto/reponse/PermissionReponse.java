package io.dexproject.achatservice.security.crud.dto.reponse;

import io.dexproject.achatservice.generic.dto.reponse.BaseReponse;
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
