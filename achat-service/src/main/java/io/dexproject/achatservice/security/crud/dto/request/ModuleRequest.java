package io.dexproject.achatservice.security.crud.dto.request;

import io.dexproject.achatservice.generic.dto.request.BaseRequest;
import io.dexproject.achatservice.security.crud.services.ModuleService;
import io.dexproject.achatservice.validators.unique.UniqueValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleRequest extends BaseRequest {
    @NotBlank(message = "le nom du module est obligatoire")
    @UniqueValidator(service = ModuleService.class, fieldName = "name", message = "Le nom {} est déjà utilisé")
    private String name;
    private String description;
}
