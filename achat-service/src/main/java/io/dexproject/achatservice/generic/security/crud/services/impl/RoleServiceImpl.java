package io.dexproject.achatservice.generic.security.crud.services.impl;

import io.dexproject.achatservice.generic.exceptions.RessourceNotFoundException;
import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.mapper.ObjectMapperUtils;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitAddRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionFormRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Droit;
import io.dexproject.achatservice.generic.security.crud.entities.Module;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import io.dexproject.achatservice.generic.security.crud.repositories.DroitRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.ModuleRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.PermissionRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.RoleRepository;
import io.dexproject.achatservice.generic.security.crud.services.RoleService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl extends ServiceGenericImpl<RoleRequest, RoleReponse, Role> implements RoleService {
    private final RoleRepository roleRepository;
    private final DroitRepository droitRepository;
    private final ModuleRepository moduleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(JpaEntityInformation<Role, Long> entityInformation, GenericRepository<Role> repository, GenericMapper<RoleRequest, RoleReponse, Role> mapper, RoleRepository roleRepository, DroitRepository droitRepository, ModuleRepository moduleRepository, PermissionRepository permissionRepository) {
        super(entityInformation, repository, mapper);
        this.roleRepository = roleRepository;
        this.droitRepository = droitRepository;
        this.moduleRepository = moduleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PermissionReponse changeAutorisation(PermissionFormRequest dto) {
        Permission permission = permissionRepository.findById(dto.getPermissionId()).orElseThrow(
                () -> new RessourceNotFoundException("La permission avec l'id " + dto.getPermissionId() + " n'existe pas!")
        );
        permission.setHasDroit(dto.getHasPermission());
        permission = permissionRepository.save(permission);
        // Mapper Dto
        return ObjectMapperUtils.map(permission, PermissionReponse.class);
    }

    @Override
    public DroitReponse changeIsDefaultDroit(DroitFormRequest dto) {
        Droit droit = droitRepository.findById(dto.getDroitId()).orElseThrow(
                () -> new RessourceNotFoundException("Le droit avec l'id " + dto.getDroitId() + " n'existe pas!")
        );
        droit.setIsDefault(dto.getIsDefault());
        droit = droitRepository.save(droit);
        if (droit.getIsDefault()) {
            List<Permission> permissions = permissionRepository.findAllByDroit(droit);
            permissions.forEach(permission -> permission.setHasDroit(true));
            permissionRepository.saveAll(permissions);
        }
        // Mapper Dto
        return ObjectMapperUtils.map(droit, DroitReponse.class);
    }

    @Override
    public void addDroit(DroitAddRequest dto) {
        Module module = moduleRepository.findByName(dto.getModule()).orElse(null);
        if (module == null) {
            module = moduleRepository.save(new Module(dto.getModule(), ""));
        }
        Optional<Droit> exist = droitRepository.findByKey(dto.getKey());
        if (!exist.isPresent()) {
            Droit droit = new Droit(dto.getKey(), dto.getLibelle(), dto.getVerbe(), "", dto.getIsDefault(), module);
            droit = droitRepository.save(droit);
            for (Role role : roleRepository.findAll()) {
                Permission permission = new Permission(role, droit, role.getIsSuper());
                permissionRepository.save(permission);
            }
        }
    }
}
