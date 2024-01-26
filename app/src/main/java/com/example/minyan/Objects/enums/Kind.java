package com.example.minyan.Objects.enums;

public enum Kind {
    SHAHRIT("שחרית"),
    MINHA("מנחה"),
    MARIV("ערבית");
    private final String displayName;

    Kind(String displayName) {
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
//
//    SHAHRIT(1) {
//        @Override
//        public String toString() {
//            return "שחרית";
//        }
//    },
//    MINHA(2) {
//        @Override
//        public String toString() {
//            return "מנחה";
//        }
//    },
//    MARIV(3) {
//        @Override
//        public String toString() {
//            return "ערבית";
//        }
//    };
//    private int kind;
//
//    private Kind(int kind) {
//        if (validatePriority(this.kind)) this.kind = kind;
//        else
//            throw new IllegalArgumentException("kind is not an integer");
//    }
//
//    public void setPriority(int kind) {
//        if (validatePriority(this.kind)) this.kind = this.kind;
//        else
//            throw new IllegalArgumentException("kind is not an integer");
//    }
//
//    public int getPriorityValue() {
//        return this.kind;
//    }
//
//    public Kind getType() {
//        return this;
//    }
//
//    /**
//     *
//     * @param kind
//     * @return whether the kind is valid or not
//     */
//    private static boolean validatePriority(int kind) {
//        if (kind <= 1 || kind > 10) return false;
//        return true;
//    }
//
//
//}
