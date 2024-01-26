package io.dexproject.achatservice.generic.security.crud.services;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.service.ServiceGeneric;

public interface PermissionService extends ServiceGeneric<PermissionRequest, PermissionReponse, Permission> {
}
