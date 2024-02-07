package io.dexproject.achatservice.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.security.crud.controllers.RoleController;
import io.dexproject.achatservice.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.security.crud.entities.Role;
import io.dexproject.achatservice.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.security.crud.services.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/roles")
@Tag(name = "Roles", description = "API de gestion des rôles")
public class RoleControllerImpl extends ControllerGenericImpl<RoleRequest, RoleReponse, Role> implements RoleController {
    protected RoleControllerImpl(RoleService service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Role newInstance() {
        return new Role();
    }
}
