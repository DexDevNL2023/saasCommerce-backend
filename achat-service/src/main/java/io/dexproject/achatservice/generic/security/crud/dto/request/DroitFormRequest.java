package io.dexproject.achatservice.generic.security.crud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DroitFormRequest {
    private Long droitId;
    private Boolean isDefault;
}
