package io.dexproject.achatservice.generic.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.generic.security.crud.controllers.PermissionController;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/permissions")
public class PermissionControllerImpl extends ControllerGenericImpl<PermissionRequest, PermissionReponse, Permission> implements PermissionController {
    protected PermissionControllerImpl(ServiceGeneric<PermissionRequest, PermissionReponse, Permission> service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Permission newInstance() {
        return new Permission();
    }
}
