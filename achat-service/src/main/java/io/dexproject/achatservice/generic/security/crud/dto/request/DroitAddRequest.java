package io.dexproject.achatservice.generic.security.crud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DroitAddRequest {
    private String module;
    private String libelle;
    private String Key;
    private String verbe;
    private Boolean isDefault;
}


