package io.dexproject.achatservice.generic.enums;

public enum RoleName {
    CUSTOMER("customer"),
    MERCHANT("merchant"),
    ADMIN("admin");

    public static List<RoleName> orderedValues = new ArrayList<>();

    static {
        orderedValues.addAll(Arrays.asList(RoleName.values()));
    }

    private final String value;

    RoleName(String value) {
        this.value = value;
    }

    public static Optional<RoleName> toEnum(String label) {
        if (label == null) {
            return Optional.empty();
        }

        for (RoleName mine : RoleName.values()) {
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
