package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModuleReponse extends BaseReponse {
    private String name;
    private String description;
}
