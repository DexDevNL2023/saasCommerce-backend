package io.dexproject.achatservice.generic.security.crud.entities.enums;

import java.util.Optional;

public interface GenericEnum<E extends Enum<E> & GenericEnum<E>> {
    Optional<E> toEnum(String label);

    String getLabel();
}
