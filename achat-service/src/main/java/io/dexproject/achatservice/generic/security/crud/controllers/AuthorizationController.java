package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitAddRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionFormRequest;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisations")
public class AuthorizationController {

    private static final String MODULE_NAME = "AUTORISATIONS";
    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PutMapping("/change-autorisation")
    public ResponseEntity<RessourceResponse> changeAutorisation(PermissionFormRequest dto) {
        authorizationService.addDroit(new DroitAddRequest(MODULE_NAME, "Changer une autorisation", "AUTORISATION-CHANGE-PERMISSION", "PUT", false));
        return new ResponseEntity<>(new RessourceResponse("Permission changée avec succès!", authorizationService.changeAutorisation(dto)), HttpStatus.OK);
    }

    @PutMapping("/make-is-default")
    public ResponseEntity<RessourceResponse> changeIsDefaultDroit(DroitFormRequest dto) {
        authorizationService.addDroit(new DroitAddRequest(MODULE_NAME, "Définir un droit par défaut", "AUTORISATION-DROIT-IS-DEFAULT", "PUT", false));
        return new ResponseEntity<>(new RessourceResponse("Permission changée avec succès!", authorizationService.changeIsDefaultDroit(dto)), HttpStatus.OK);
    }
}
