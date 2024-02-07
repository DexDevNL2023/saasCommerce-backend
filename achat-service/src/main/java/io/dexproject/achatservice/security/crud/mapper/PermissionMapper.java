package io.dexproject.achatservice.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.security.crud.entities.Permission;

public interface PermissionMapper extends GenericMapper<PermissionRequest, PermissionReponse, Permission> {
}
