package io.dexproject.achatservice.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.security.crud.controllers.ModuleController;
import io.dexproject.achatservice.security.crud.dto.reponse.ModuleReponse;
import io.dexproject.achatservice.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.security.crud.entities.Module;
import io.dexproject.achatservice.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.security.crud.services.ModuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/modules")
@Tag(name = "Modules", description = "API de gestion des modules applicatifs")
public class ModuleControllerImpl extends ControllerGenericImpl<ModuleRequest, ModuleReponse, Module> implements ModuleController {
    protected ModuleControllerImpl(ModuleService service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Module newInstance() {
        return new Module();
    }
}
