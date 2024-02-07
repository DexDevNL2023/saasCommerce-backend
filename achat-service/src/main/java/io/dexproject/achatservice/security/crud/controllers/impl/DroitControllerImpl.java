package io.dexproject.achatservice.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.security.crud.controllers.DroitController;
import io.dexproject.achatservice.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.security.crud.entities.Droit;
import io.dexproject.achatservice.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.security.crud.services.DroitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/droits")
@Tag(name = "Droits", description = "API de gestion des droits")
public class DroitControllerImpl extends ControllerGenericImpl<DroitRequest, DroitReponse, Droit> implements DroitController {
    protected DroitControllerImpl(DroitService service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Droit newInstance() {
        return new Droit();
    }
}
