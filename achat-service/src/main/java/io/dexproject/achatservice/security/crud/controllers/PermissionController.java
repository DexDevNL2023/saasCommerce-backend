package io.dexproject.achatservice.security.crud.controllers;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.security.crud.entities.Permission;

public interface PermissionController extends ControllerGeneric<PermissionRequest, PermissionReponse, Permission> {
}
