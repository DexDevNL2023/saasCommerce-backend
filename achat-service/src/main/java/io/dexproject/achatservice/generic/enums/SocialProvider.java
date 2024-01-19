package io.dexproject.achatservice.generic.enums;

public enum SocialProvider {

    FACEBOOK("facebook"),
    TWITTER("twitter"),
    LINKEDIN("linkedin"),
    GOOGLE("google"),
    GITHUB("github"),
    LOCAL("local");

    public static List<SocialProvider> orderedValues = new ArrayList<>();

    static {
        orderedValues.addAll(Arrays.asList(SocialProvider.values()));
    }

    private final String value;

    SocialProvider(String value) {
        this.value = value;
    }

    public static Optional<SocialProvider> toEnum(String label) {
        if (label == null) {
            return Optional.empty();
        }

        for (SocialProvider mine : SocialProvider.values()) {
            if (label.equals(mine.getValue())) {
                return Optional.of(mine);
            }
        }

        throw new IllegalArgumentException("no supported");
    }

    public String getValue() {
        return value;
    }
}
