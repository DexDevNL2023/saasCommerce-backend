package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.enums.EnumValue;
import io.dexproject.achatservice.generic.security.crud.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.enums.SocialProvider;
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
    public List<EnumValue> getRoleNames() {
        return RoleName.orderedValues.stream()
                .map(e -> new EnumValue(e.name(), e.getLabel()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/enums/social-provider", produces = MediaTypes.HAL_JSON_VALUE)
    public List<EnumValue> getSocialProviders() {
        return SocialProvider.orderedValues.stream()
                .map(e -> new EnumValue(e.name(), e.getLabel()))
                .collect(Collectors.toList());
    }
}
