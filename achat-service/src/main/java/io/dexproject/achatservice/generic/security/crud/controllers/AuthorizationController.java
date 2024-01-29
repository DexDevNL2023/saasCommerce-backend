package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitAddRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionFormRequest;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisations")
@Tag(name = "Authorisations", description = "API de gestion des authorisations")
public class AuthorizationController {

    private static final String MODULE_NAME = "AUTORISATIONS";
    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PutMapping("/role-autorisations/{roleId}")
    public ResponseEntity<RessourceResponse> getAllAutorisations(@NotNull @PathVariable("roleId") Long roleId) {
        authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher les permissions d'un role", MODULE_NAME+"-GET-PERMISSION-ROLE", "GET", false));
        return new ResponseEntity<>(new RessourceResponse("Permissions trouvées avec succès!", authorizationService.getAutorisations(roleId)), HttpStatus.OK);
    }

    @PutMapping("/change-autorisation")
    public ResponseEntity<RessourceResponse> changeAutorisation(@NotEmpty @Valid @RequestBody PermissionFormRequest dto) {
        authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Changer une autorisation", MODULE_NAME+"-CHANGE-PERMISSION", "PUT", false));
        return new ResponseEntity<>(new RessourceResponse("Permission changée avec succès!", authorizationService.changeAutorisation(dto)), HttpStatus.OK);
    }

    @PutMapping("/make-is-default")
    public ResponseEntity<RessourceResponse> changeIsDefaultDroit(@NotEmpty @Valid @RequestBody DroitFormRequest dto) {
        authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Définir un droit par défaut", MODULE_NAME+"-DROIT-IS-DEFAULT", "PUT", false));
        return new ResponseEntity<>(new RessourceResponse("Permission changée avec succès!", authorizationService.changeIsDefaultDroit(dto)), HttpStatus.OK);
    }
}
