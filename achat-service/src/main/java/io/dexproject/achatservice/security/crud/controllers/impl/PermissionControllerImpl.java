package io.dexproject.achatservice.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.security.crud.controllers.PermissionController;
import io.dexproject.achatservice.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.security.crud.dto.request.PermissionRequest;
import io.dexproject.achatservice.security.crud.entities.Permission;
import io.dexproject.achatservice.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.security.crud.services.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/permissions")
@Tag(name = "Permissions", description = "API de gestion des permissions")
public class PermissionControllerImpl extends ControllerGenericImpl<PermissionRequest, PermissionReponse, Permission> implements PermissionController {
    protected PermissionControllerImpl(PermissionService service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Permission newInstance() {
        return new Permission();
    }
}
