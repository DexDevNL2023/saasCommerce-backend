package io.dexproject.achatservice.generic.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Role;

public interface RoleMapper extends GenericMapper<RoleRequest, RoleReponse, Role> {
}
