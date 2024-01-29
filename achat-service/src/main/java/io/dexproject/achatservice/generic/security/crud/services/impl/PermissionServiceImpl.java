package io.dexproject.achatservice.generic.security.crud.services.impl;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.security.crud.mapper.PermissionMapper;
import io.dexproject.achatservice.generic.security.crud.repositories.PermissionRepository;
import io.dexproject.achatservice.generic.security.crud.services.PermissionService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionServiceImpl extends ServiceGenericImpl<PermissionRequest, PermissionReponse, Permission> implements PermissionService {
    public PermissionServiceImpl(JpaEntityInformation<Permission, Long> entityInformation, PermissionRepository repository, PermissionMapper mapper) {
        super(entityInformation, repository, mapper);
    }
}
