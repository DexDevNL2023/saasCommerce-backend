package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Role;

public interface RoleController extends ControllerGeneric<RoleRequest, RoleReponse, Role> {
}
