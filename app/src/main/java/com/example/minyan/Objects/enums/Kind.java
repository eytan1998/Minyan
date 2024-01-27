package com.example.minyan.Objects.enums;

public enum Kind {
    SHAHRIT("שחרית",0),
    MINHA("מנחה",1),
    MARIV("ערבית",2);
    private final String displayName;
    private final int index;


    Kind(String displayName,int index) {
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
