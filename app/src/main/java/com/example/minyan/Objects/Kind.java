package com.example.minyan.Objects;

public enum Kind {
    //todo
    COMPUTATIONAL(1) {
        @Override
        public String toString() {
            return "Computational Ex2_2.Task";
        }
    },
    IO(2) {
        @Override
        public String toString() {
            return "IO-Bound Ex2_2.Task";
        }
    },
    OTHER(3) {
        @Override
        public String toString() {
            return "Unknown Ex2_2.Task";
        }
    };
    private int typePriority;

    private Kind(int priority) {
        if (validatePriority(priority)) typePriority = priority;
        else
            throw new IllegalArgumentException("Priority is not an integer");
    }

    public void setPriority(int priority) {
        if (validatePriority(priority)) this.typePriority = priority;
        else
            throw new IllegalArgumentException("Priority is not an integer");
    }

    public int getPriorityValue() {
        return typePriority;
    }

    public Kind getType() {
        return this;
    }

    /**
     * priority is represented by an integer value, ranging from 1 to 10
     *
     * @param priority
     * @return whether the priority is valid or not
     */
    private static boolean validatePriority(int priority) {
        if (priority < 1 || priority > 10) return false;
        return true;
    }


}
