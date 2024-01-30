package io.dexproject.achatservice.generic.security.crud.dto.request;

import io.dexproject.achatservice.generic.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.services.RoleService;
import io.dexproject.achatservice.generic.validators.EnumValidator;
import io.dexproject.achatservice.generic.validators.UniqueValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest extends BaseRequest {
    @EnumValidator(enumClass = RoleName.class)
    @UniqueValidator(service = RoleService.class, fieldName = "libelle", message = "Le libelle {} est déjà utilisé")
    private RoleName libelle;
    private Boolean isSuper = false;
}
