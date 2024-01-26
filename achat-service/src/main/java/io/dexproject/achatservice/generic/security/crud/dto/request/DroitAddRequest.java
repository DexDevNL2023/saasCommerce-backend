package io.dexproject.achatservice.generic.security.crud.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DroitAddRequest {
    private String module;
    private String libelle;
    private String Key;
    private String verbe;
    private Boolean isDefault;
}


