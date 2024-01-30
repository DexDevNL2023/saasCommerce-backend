package io.dexproject.achatservice.generic.security.crud.mapper.impl;

import io.dexproject.achatservice.generic.mapper.impl.GenericMapperImpl;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Module;
import io.dexproject.achatservice.generic.security.crud.mapper.ModuleMapper;
import io.dexproject.achatservice.generic.security.crud.repositories.ModuleRepository;
import org.mapstruct.Mapper;

@Mapper
public class ModuleMapperImpl extends GenericMapperImpl<ModuleRequest, ModuleReponse, Module> implements ModuleMapper {

    protected ModuleMapperImpl(Class<Module> entityClass, Class<ModuleReponse> dtoClass, ModuleRepository repository) {
        super(entityClass, dtoClass, repository);
    }

    @Override
    protected Module newInstance() {
        return new Module();
    }
}
