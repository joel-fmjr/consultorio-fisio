package com.joel.consultorio_fisio.appointment;

public enum AppointmentDuration {
    ONE_HOUR("1h", 60),
    ONE_HOUR_THIRTY("1h30", 90),
    TWO_HOURS("2h", 120);

    private final String displayName;
    private final int minutes;

    AppointmentDuration(String displayName, int minutes) {
        this.displayName = displayName;
        this.minutes = minutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinutes() {
        return minutes;
    }
}
