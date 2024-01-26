package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface RoleController extends ControllerGeneric<RoleRequest, RoleReponse, Role> {

    ResponseEntity<RessourceResponse> changeAutorisation(@RequestBody PermissionFormRequest dto);

    ResponseEntity<RessourceResponse> changeIsDefaultDroit(@RequestBody DroitFormRequest dto);
}
