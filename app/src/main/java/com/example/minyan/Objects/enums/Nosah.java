package com.example.minyan.Objects.enums;

public enum Nosah {

    SHAHRIT(1) {
        @Override
        public String toString() {
            return "שחרית";
        }
    },
    MINHA(2) {
        @Override
        public String toString() {
            return "מנחה";
        }
    },
    MARIV(3) {
        @Override
        public String toString() {
            return "ערבית";
        }
    };
    private int nosah;

    private Nosah(int nosah) {
        if (validatePriority(this.nosah)) this.nosah = nosah;
        else
            throw new IllegalArgumentException("nosah is not an integer");
    }

    public void setPriority(int nosah) {
        if (validatePriority(this.nosah)) this.nosah = this.nosah;
        else
            throw new IllegalArgumentException("nosah is not an integer");
    }

    public int getPriorityValue() {
        return this.nosah;
    }

    public Nosah getType() {
        return this;
    }

    /**
     * @param nosah
     * @return whether the nosah is valid or not
     */
    private static boolean validatePriority(int nosah) {
        if (nosah < 1 || nosah > 10) return false;
        return true;
    }


}
