package com.example.minyan.Objects.enums;


import com.example.minyan.R;

public enum Nosah {
    ALL("כל נוסח", 0, R.drawable.ic_marker_all),
    ASHKENAZ("אשכנז", 1, R.drawable.ic_marker_askenaz),
    SPARD("ספרד", 1, R.drawable.ic_marker_spard),
    SPARADI("ספרדי", 1, R.drawable.ic_marker_spradi),
    BALADY("בלדי", 1, R.drawable.ic_marker_balady),
    SHAMI("שאמי", 1, R.drawable.ic_marker_shami);
    private final String displayName;
    private final int index;
    private final int icMarkerId;


    Nosah(String displayName, int index, int icMarkerId) {
        this.displayName = displayName;
        this.index = index;
        this.icMarkerId = icMarkerId;
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

    public int getIconId() {
        return icMarkerId;
    }
}
