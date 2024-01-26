package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleReponse extends BaseReponse {
    private String name;
    private String description;
}
