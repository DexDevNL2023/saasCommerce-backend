package io.dexproject.achatservice.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.security.crud.entities.Role;

public interface RoleMapper extends GenericMapper<RoleRequest, RoleReponse, Role> {
}
