package io.dexproject.achatservice.generic.security.crud.mapper.impl;

import io.dexproject.achatservice.generic.mapper.impl.GenericMapperImpl;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import io.dexproject.achatservice.generic.security.crud.mapper.RoleMapper;
import io.dexproject.achatservice.generic.security.crud.repositories.RoleRepository;
import org.mapstruct.Mapper;

@Mapper
public class RoleMapperImpl extends GenericMapperImpl<RoleRequest, RoleReponse, Role> implements RoleMapper {
    protected RoleMapperImpl(Class<Role> entityClass, Class<RoleReponse> dtoClass, RoleRepository repository) {
        super(entityClass, dtoClass, repository);
    }

    @Override
    protected Role newInstance() {
        return new Role();
    }
}
