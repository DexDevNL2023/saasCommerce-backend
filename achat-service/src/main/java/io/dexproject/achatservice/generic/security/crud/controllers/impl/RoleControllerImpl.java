package io.dexproject.achatservice.generic.security.crud.controllers.impl;

import io.dexproject.achatservice.generic.controller.impl.ControllerGenericImpl;
import io.dexproject.achatservice.generic.security.crud.controllers.RoleController;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RoleReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.generic.security.crud.services.RoleService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/autorisation/roles")
public class RoleControllerImpl extends ControllerGenericImpl<RoleRequest, RoleReponse, Role> implements RoleController {
    protected RoleControllerImpl(RoleService service, AuthorizationService authorizationService) {
        super(service, authorizationService);
    }

    @Override
    protected Role newInstance() {
        return new Role();
    }
}
