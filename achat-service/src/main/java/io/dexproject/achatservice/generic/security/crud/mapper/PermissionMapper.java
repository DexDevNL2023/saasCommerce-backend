package io.dexproject.achatservice.generic.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;

public interface PermissionMapper extends GenericMapper<PermissionRequest, PermissionReponse, Permission> {
}
