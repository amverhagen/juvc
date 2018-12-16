package com.andrewverhagen.juvc.connection;

public enum ConnectionState {

    UNOPENED(0, false),
    CONNECTING(1, true),
    CONNECTED(2, true),
    CLOSED(3, false);

    private final int connectionLevel;
    private final boolean isOpen;

    ConnectionState(int connectionLevel, boolean isOpen) {
        this.connectionLevel = connectionLevel;
        this.isOpen = isOpen;
    }

    public boolean canMoveToConnectionLevel(ConnectionState desiredState) {
        return this.connectionLevel < desiredState.connectionLevel;
    }

    public boolean isAnOpenState() {
        return this.isOpen;
    }
}
