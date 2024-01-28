package io.dexproject.achatservice.generic.security.crud.services.impl;

import io.dexproject.achatservice.generic.mapper.AbstractGenericMapper;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.repositories.RoleRepository;
import io.dexproject.achatservice.generic.security.crud.services.RoleService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleServiceImpl extends ServiceGenericImpl<RoleRequest, RoleReponse, Role> implements RoleService {

    private  final RoleRepository repository;

    public RoleServiceImpl(JpaEntityInformation<Role, Long> entityInformation, RoleRepository repository, AbstractGenericMapper<RoleRequest, RoleReponse, Role> mapper) {
        super(entityInformation, repository, mapper);
        this.repository = repository;
    }

    @Override
    public void addDefaultRoles() {
        for (RoleName roleName : RoleName.orderedValues) {
            if (repository.findByRoleName(roleName) == null) {
                Role authority = new Role();
                authority = new Role();
                authority.setIsSuper(roleName.equals(RoleName.ADMIN));
                authority.setIsGrant(false);
                authority.setLibelle(roleName);
                repository.save(authority);
            }
        }
    }
}
