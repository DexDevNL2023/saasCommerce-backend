package io.dexproject.achatservice.generic.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SocialProvider implements GenericEnum<SocialProvider> {
    FACEBOOK("facebook"),
    TWITTER("twitter"),
    LINKEDIN("linkedin"),
    GOOGLE("google"),
    GITHUB("github"),
    LOCAL("local");

    public static final List<SocialProvider> orderedValues = new ArrayList<>();
    private final String label;

    static {
        orderedValues.addAll(Arrays.asList(SocialProvider.values()));
    }

    SocialProvider(String label) {
        this.label = label;
    }

    @Override
    @JsonValue
    public String getLabel() {
        return this.label;
    }

    @Override
    @JsonCreator
    public Optional<SocialProvider> toEnum(String label) {
        return Stream.of(SocialProvider.values()).filter(e -> e.getLabel().equals(label)).findFirst();
    }
}
