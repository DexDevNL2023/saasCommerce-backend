package io.dexproject.achatservice.generic.enums;

import java.util.List;
import java.util.Optional;

public interface GenericEnum<E extends Enum<E> & GenericEnum<E>> {
    List<E> orderedValues();

    String getValue();

    Optional<E> toEnum(String value);
}
