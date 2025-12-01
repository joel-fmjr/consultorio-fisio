package com.joel.consultorio_fisio.assessment;

public enum EatingHabits {
    HEALTHY("Saudável"),
    MODERATE("Moderado"),
    POOR("Ruim");

    private final String displayName;

    EatingHabits(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
