package io.dexproject.achatservice.generic.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Module;

public interface ModuleMapper extends GenericMapper<ModuleRequest, ModuleReponse, Module> {
}
