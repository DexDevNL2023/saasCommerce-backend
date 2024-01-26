package io.dexproject.achatservice.generic.security.crud.dto.request;

import io.dexproject.achatservice.generic.security.crud.services.DroitService;
import io.dexproject.achatservice.generic.validators.UniqueValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DroitRequest {
    private Long id;
    @NotBlank(message = "La clé du droit est obligatoire")
    @Size(min = 2, message = "La clé doit être d'au moins 2 caractères")
    @UniqueValidator(service = DroitService.class, fieldName = "key", message = "La clé {} est déjà utilisé")
    private String key;
    @NotBlank(message = "le libelle du droit est obligatoire")
    private String libelle;
    @NotBlank(message = "La verbe du droit est obligatoire")
    private String verbe;
    private String description;
    private Long moduleId;
    private Boolean isDefault;
}
