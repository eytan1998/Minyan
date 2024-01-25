package com.example.minyan.Objects.enums;

public enum Nosah {

    ALL("all"),
    ASHKENAZ("askenaz");
    private final String displayName;

    Nosah(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
