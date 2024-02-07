package io.dexproject.achatservice.security.crud.controllers;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.security.crud.entities.Module;

public interface ModuleController extends ControllerGeneric<ModuleRequest, ModuleReponse, Module> {
}
