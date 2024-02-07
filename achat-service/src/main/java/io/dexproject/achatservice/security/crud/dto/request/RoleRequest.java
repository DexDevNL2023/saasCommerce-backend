package io.dexproject.achatservice.security.crud.dto.request;

import io.dexproject.achatservice.generic.dto.request.BaseRequest;
import io.dexproject.achatservice.security.crud.enums.RoleName;
import io.dexproject.achatservice.security.crud.services.RoleService;
import io.dexproject.achatservice.validators.enumaration.EnumValidator;
import io.dexproject.achatservice.validators.unique.UniqueValidator;
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
