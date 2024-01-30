package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import io.dexproject.achatservice.generic.dto.reponse.BaseReponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DroitReponse extends BaseReponse {
    private Long id;
    private String libelle;
    private String key;
    private String verbe;
    private Boolean isDefault;
    private String description;
    private ModuleReponse module;
}
