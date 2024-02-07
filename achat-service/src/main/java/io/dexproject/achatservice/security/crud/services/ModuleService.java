package io.dexproject.achatservice.security.crud.services;

import io.dexproject.achatservice.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.security.crud.entities.Module;
import io.dexproject.achatservice.generic.service.ServiceGeneric;

public interface ModuleService extends ServiceGeneric<ModuleRequest, ModuleReponse, Module> {
}
