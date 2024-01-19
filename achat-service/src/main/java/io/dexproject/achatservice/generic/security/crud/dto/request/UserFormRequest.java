package io.dexproject.achatservice.generic.security.crud.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UserFormRequest extends BaseRequestDto {
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;
    @NotEmpty
    private String firstName;
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "le format de l'email est incorrecte", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    private String phone;
    private String adresse;
    private boolean usingQr;
    private String langKey;
    @Size(max = 256, message = "La taille de l'image doit être inférieur ou égale à 256")
    private String imageUrl;
}
