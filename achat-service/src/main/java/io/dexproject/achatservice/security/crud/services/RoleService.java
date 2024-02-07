package io.dexproject.achatservice.security.crud.services;

import io.dexproject.achatservice.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.security.crud.entities.Role;
import io.dexproject.achatservice.generic.service.ServiceGeneric;

public interface RoleService extends ServiceGeneric<RoleRequest, RoleReponse, Role> {
    void addDefaultRoles();
}
