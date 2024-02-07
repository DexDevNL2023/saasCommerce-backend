package io.dexproject.achatservice.security.crud.controllers;

import io.dexproject.achatservice.generic.enums.EnumValue;
import io.dexproject.achatservice.security.crud.enums.RoleName;
import io.dexproject.achatservice.security.crud.enums.SocialProvider;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/enums")
@RestController
@Slf4j
public class EnumController {
    //Ennumerateur
    @GetMapping(value = "/enums/roles", produces = MediaTypes.HAL_JSON_VALUE)
    @SecurityRequirement(name = "Authorization")
    public List<EnumValue> getRoleNames() {
        return RoleName.valuesInList().stream()
                .map(e -> new EnumValue(e.name(), e.getValue()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/enums/social-provider", produces = MediaTypes.HAL_JSON_VALUE)
    @SecurityRequirement(name = "Authorization")
    public List<EnumValue> getSocialProviders() {
        return SocialProvider.valuesInList().stream()
                .map(e -> new EnumValue(e.name(), e.getValue()))
                .collect(Collectors.toList());
    }
}
