package io.dexproject.achatservice.generic.enums;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.hateoas.server.core.Relation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Relation(collectionRelation = "enums")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class GenericEnumeration<E extends Enum<E> & GenericEnumeration<E>> {
    private final Class<E> enumCls;
    private final String value;

    GenericEnumeration(Class<E> enumCls, String value) {
        this.enumCls = enumCls;
        this.value = value;
    }

    static <T> List<T> enumValuesInList(Class<T> enumCls) {
        T[] arr = enumCls.getEnumConstants();
        return arr == null ? Collections.emptyList() : Arrays.asList(arr);
    }

    public List<E> orderedValues() {
        return enumValuesInList(enumCls);
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public Optional<E> toEnum(String value) {
        return orderedValues().stream().filter(e -> e.getValue().equals(value)).findFirst();
    }
}
