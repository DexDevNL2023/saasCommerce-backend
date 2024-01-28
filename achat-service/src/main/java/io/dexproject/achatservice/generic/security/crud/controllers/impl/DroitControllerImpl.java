package io.dexproject.achatservice.generic.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.generic.security.crud.controllers.DroitController;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Droit;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/droits")
public class DroitControllerImpl extends ControllerGenericImpl<DroitRequest, DroitReponse, Droit> implements DroitController {
    protected DroitControllerImpl(ServiceGeneric<DroitRequest, DroitReponse, Droit> service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Droit newInstance() {
        return new Droit();
    }
}
