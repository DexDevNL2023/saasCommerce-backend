package io.dexproject.achatservice.generic.enums;

import java.util.Optional;

public interface GenericEnum<E extends Enum<E> & GenericEnum<E>> {
    Optional<E> toEnum(String label);

    String toLabel(E enumaration);

    String getLabel();
}
