package com.andrewverhagen.juvc.holder;

import com.andrewverhagen.juvc.connection.VirtualConnection;

import java.net.DatagramPacket;
import java.util.Vector;

public class ConnectionHolder {

    private final Vector<VirtualConnection> virtualConnections;
    private final int maxAmountOfConnections;

    public ConnectionHolder(int maxAmountOfConnections) {
        if (maxAmountOfConnections < 1)
            throw new IllegalArgumentException("Holder must have a max size of at least one.");
        this.maxAmountOfConnections = maxAmountOfConnections;
        this.virtualConnections = new Vector<>();
    }

    public synchronized void addConnection(VirtualConnection connectionToAdd) throws HolderIsFullException, AlreadyHoldingConnectionException {
        if (this.holdingConnection(connectionToAdd))
            throw new AlreadyHoldingConnectionException();
        if (this.atCapacity())
            throw new HolderIsFullException();
        this.virtualConnections.add(connectionToAdd);
    }

    public synchronized boolean atCapacity() {
        return this.virtualConnections.size() >= this.maxAmountOfConnections;
    }

    public synchronized boolean holdingConnection(VirtualConnection connectionToCheck) {
        for (VirtualConnection virtualConnection : virtualConnections)
            if (virtualConnection.containsAddress(connectionToCheck))
                return true;
        return false;
    }

    public synchronized boolean handleInput(DatagramPacket inputPacket) {
        for (VirtualConnection virtualConnection : virtualConnections)
            if (virtualConnection.handleInput(inputPacket))
                return true;
        return false;
    }

    public synchronized void sendOutputToConnections() {
        for (VirtualConnection virtualConnection : virtualConnections)
            virtualConnection.sendOutput();
    }

    class AlreadyHoldingConnectionException extends Exception {
    }

    class HolderIsFullException extends Exception {

    }
}
