package io.dexproject.achatservice.generic.security.crud.services.impl;

import io.dexproject.achatservice.generic.mapper.AbstractGenericMapper;
import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Module;
import io.dexproject.achatservice.generic.security.crud.services.ModuleService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModuleServiceImpl extends ServiceGenericImpl<ModuleRequest, ModuleReponse, Module> implements ModuleService {
    public ModuleServiceImpl(JpaEntityInformation<Module, Long> entityInformation, GenericRepository<Module> repository, AbstractGenericMapper<ModuleRequest, ModuleReponse, Module> mapper) {
        super(entityInformation, repository, mapper);
    }
}
