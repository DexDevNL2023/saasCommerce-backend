package io.dexproject.achatservice.generic.security.crud.dto.request;

import io.dexproject.achatservice.generic.security.crud.services.RoleService;
import io.dexproject.achatservice.generic.validators.UniqueValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest extends BaseRequest {
    @NotBlank(message = "le libelle du role est obligatoire")
    @UniqueValidator(service = RoleService.class, fieldName = "libelle", message = "Le libelle {} est déjà utilisé")
    private String libelle;
    private Boolean isSuper;
    private Boolean isGrant = false;
}
