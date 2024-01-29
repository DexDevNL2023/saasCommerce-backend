package io.dexproject.achatservice.generic.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.generic.security.crud.controllers.ModuleController;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Module;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.generic.security.crud.services.ModuleService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/modules")
public class ModuleControllerImpl extends ControllerGenericImpl<ModuleRequest, ModuleReponse, Module> implements ModuleController {
    protected ModuleControllerImpl(ModuleService service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Module newInstance() {
        return new Module();
    }
}
