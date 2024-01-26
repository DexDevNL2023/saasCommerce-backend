package io.dexproject.achatservice.generic.security.crud.dto.request;

import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import io.dexproject.achatservice.generic.validators.UniqueValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest extends BaseRequest {
    private String emailOrPhone;
    @NotBlank(message = "Le nom est obligatoire")
    @UniqueValidator(service = UserAccountService.class, fieldName = "lastName", message = "Le nom {} est déjà utilisé")
    private String lastName;
    private String firstName;
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "le format de l'email est incorrecte", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @UniqueValidator(service = UserAccountService.class, fieldName = "email", message = "L'e-mail {} est déjà utilisé")
    private String email;
    @UniqueValidator(service = UserAccountService.class, fieldName = "phone", message = "Le téléphone {} est déjà utilisé")
    private String phone;
    private String adresse;
    private boolean usingQr = false;
    private String langKey;
    @Size(max = 256, message = "La taille de l'image doit être inférieur ou égale à 256")
    private String imageUrl;

    public String getEmailOrPhone() {
        return this.emailOrPhone = this.email.isEmpty() ? this.phone : this.email;
    }
}
