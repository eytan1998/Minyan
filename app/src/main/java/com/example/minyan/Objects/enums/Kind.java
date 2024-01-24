package com.example.minyan.Objects.enums;

public enum Kind {
    ALL(1) {
        @Override
        public String toString() {
            return "all";
        }
    },
    Ashkenaz(2) {
        @Override
        public String toString() {
            return "Ashkenaz";
        }
    },
    Sparad(3) {
        @Override
        public String toString() {
            return "Sparad";
        }
    };
    private int kind;

    private Kind(int kind) {
        if (validatePriority(this.kind)) this.kind = kind;
        else
            throw new IllegalArgumentException("kind is not an integer");
    }

    public void setPriority(int kind) {
        if (validatePriority(this.kind)) this.kind = this.kind;
        else
            throw new IllegalArgumentException("kind is not an integer");
    }

    public int getPriorityValue() {
        return this.kind;
    }

    public Kind getType() {
        return this;
    }

    /**
     *
     * @param kind
     * @return whether the kind is valid or not
     */
    private static boolean validatePriority(int kind) {
        if (kind < 1 || kind > 10) return false;
        return true;
    }


}
