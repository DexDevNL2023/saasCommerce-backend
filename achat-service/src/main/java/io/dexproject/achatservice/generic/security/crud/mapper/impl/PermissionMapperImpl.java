package io.dexproject.achatservice.generic.security.crud.mapper.impl;

import io.dexproject.achatservice.generic.mapper.impl.GenericMapperImpl;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.security.crud.mapper.PermissionMapper;
import org.mapstruct.Mapper;

@Mapper
public class PermissionMapperImpl extends GenericMapperImpl<PermissionRequest, PermissionReponse, Permission> implements PermissionMapper {
    protected PermissionMapperImpl(Class<Permission> entityClass, Class<PermissionReponse> dtoClass, GenericRepository<Permission> repository) {
        super(entityClass, dtoClass, repository);
    }

    @Override
    protected Permission newInstance() {
        return new Permission();
    }
}
