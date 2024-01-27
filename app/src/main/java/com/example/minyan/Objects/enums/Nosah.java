package com.example.minyan.Objects.enums;

public enum Nosah {

    ALL("כל נוסח", 0),
    ASHKENAZ("אשכנז", 1);
    private final String displayName;
    private final int index;


    Nosah(String displayName, int index) {
        this.displayName = displayName;
        this.index = index;
    }



    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public int getIntValue() {
        return this.index;
    }
}
