package io.dexproject.achatservice.security.crud.services.impl;

import io.dexproject.achatservice.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.security.crud.entities.Module;
import io.dexproject.achatservice.security.crud.mapper.ModuleMapper;
import io.dexproject.achatservice.security.crud.repositories.ModuleRepository;
import io.dexproject.achatservice.security.crud.services.ModuleService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModuleServiceImpl extends ServiceGenericImpl<ModuleRequest, ModuleReponse, Module> implements ModuleService {
    public ModuleServiceImpl(JpaEntityInformation<Module, Long> entityInformation, ModuleRepository repository, ModuleMapper mapper) {
        super(entityInformation, repository, mapper);
    }
}
