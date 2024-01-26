package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;

public interface PermissionController extends ControllerGeneric<PermissionRequest, PermissionReponse, Permission> {
}
