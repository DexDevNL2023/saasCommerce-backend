package io.dexproject.achatservice.generic.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.generic.security.crud.controllers.RoleController;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitAddRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import io.dexproject.achatservice.generic.security.crud.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/roles")
public class RoleControllerImpl extends ControllerGenericImpl<RoleRequest, RoleReponse, Role> implements RoleController {

    private static final String MODULE_NAME = "AUTORISATIONS";
    private final RoleService service;

    public RoleControllerImpl(RoleService service) {
        super(service);
        this.service = service;
    }

    @Override
    @PutMapping("/change-autorisation")
    public ResponseEntity<RessourceResponse> changeAutorisation(PermissionFormRequest dto) {
        service.addDroit(new DroitAddRequest(MODULE_NAME, "Changer une autorisation", "autorisation-change-permission", "POST", false));
        return new ResponseEntity<>(new RessourceResponse("Permission changée avec succès!", service.changeAutorisation(dto)), HttpStatus.OK);
    }

    @Override
    @PutMapping("/make-is-default")
    public ResponseEntity<RessourceResponse> changeIsDefaultDroit(DroitFormRequest dto) {
        service.addDroit(new DroitAddRequest(MODULE_NAME, "Définir un droit par défaut", "autorisation-droit-is-default", "PUT", false));
        return new ResponseEntity<>(new RessourceResponse("Permission changée avec succès!", service.changeIsDefaultDroit(dto)), HttpStatus.OK);
    }
}
