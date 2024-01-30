package io.dexproject.achatservice.generic.security.crud.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import io.dexproject.achatservice.generic.enums.GenericEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RoleName implements GenericEnum<RoleName> {
    PARTNER("partner"),
    CUSTOMER("customer"),
    MERCHANT("merchant"),
    ADMIN("admin");

    private final String label;

    public static final List<RoleName> orderedValues = new ArrayList<>();

    static {
        orderedValues.addAll(Arrays.asList(RoleName.values()));
    }

    RoleName(String label) {
        this.label = label;
    }

    @Override
    @JsonValue
    public String getLabel() {
        return this.label;
    }

    @Override
    @JsonCreator
    public Optional<RoleName> toEnum(String label) {
        return Stream.of(RoleName.values()).filter(e -> e.getLabel().equals(label)).findFirst();
    }
}
