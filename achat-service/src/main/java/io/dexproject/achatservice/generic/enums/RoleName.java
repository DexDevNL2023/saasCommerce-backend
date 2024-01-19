package io.dexproject.achatservice.generic.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RoleName implements GenericEnum<RoleName> {
    CUSTOMER("customer"),
    MERCHANT("merchant"),
    ADMIN("admin");

    private final String label;

    RoleName(String label) {
        this.label = label;
    }

    @Override
    @JsonCreator
    public Optional<RoleName> toEnum(String label) {
        return Stream.of(RoleName.values()).filter(e -> e.getLabel().equals(label)).findFirst();
    }

    @Override
    public String toLabel(RoleName enumaration) {
        return Stream.of(RoleName.values()).filter(e -> e.getLabel().equals(label)).findFirst();
    }

    @Override
    @JsonValue
    public String getLabel() {
        return this.label;
    }

    @Override
    public List<RoleName> getAll() {
        return Arrays.asList(RoleName.values());
    }
}
