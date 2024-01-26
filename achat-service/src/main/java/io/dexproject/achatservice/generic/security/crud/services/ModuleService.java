package io.dexproject.achatservice.generic.security.crud.services;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Module;
import io.dexproject.achatservice.generic.service.ServiceGeneric;

public interface ModuleService extends ServiceGeneric<ModuleRequest, ModuleReponse, Module> {
}
