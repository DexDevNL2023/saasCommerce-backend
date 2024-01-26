package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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