package io.dexproject.achatservice.generic.security.crud.services.impl;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.security.crud.services.PermissionService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionServiceImpl extends ServiceGenericImpl<PermissionRequest, PermissionReponse, Permission> implements PermissionService {
    public PermissionServiceImpl(JpaEntityInformation<Permission, Long> entityInformation, GenericRepository<Permission> repository, GenericMapper<PermissionRequest, PermissionReponse, Permission> mapper) {
        super(entityInformation, repository, mapper);
    }
}
