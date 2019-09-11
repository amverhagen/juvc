package com.andrewverhagen.juvc.holder;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.andrewverhagen.juvc.connection.DatagramPacketConsumer;
import com.andrewverhagen.juvc.connection.VirtualConnection;

public class ConnectionPool implements DatagramPacketConsumer, DatagramPacketSupplier {

    private final ClosedConnectionRemover closedConnectionRemover;
    private final HashSet<VirtualConnection> virtualConnections = new HashSet<>();
    private final HashSet<ManagedVirtualConnection> connections = new HashSet<>();
    private final int maxAmountOfConnections;

    public ConnectionPool(int maxAmountOfConnections) {
        if (maxAmountOfConnections < 1)
            throw new IllegalArgumentException("ConnectionPool must have a max size of at least 1.");
        this.maxAmountOfConnections = maxAmountOfConnections;
        this.closedConnectionRemover = new ClosedConnectionRemover();
    }

    public void addConnection(VirtualConnection connectionToAdd)
            throws HolderIsFullException, AlreadyHoldingConnectionException {
        synchronized (this.virtualConnections) {
            if (this.holdingConnection(connectionToAdd))
                throw new AlreadyHoldingConnectionException();
            if (this.atCapacity())
                throw new HolderIsFullException();
            if (this.virtualConnections.add(connectionToAdd)) {
                connectionToAdd.addObserver(closedConnectionRemover);
            }
        }
    }

    public boolean atCapacity() {
        synchronized (this.virtualConnections) {
            return this.virtualConnections.size() >= this.maxAmountOfConnections;
        }
    }

    public boolean holdingConnection(VirtualConnection connectionToCheck) {
        this.removeClosedConnections();
        synchronized (virtualConnections) {
            return this.virtualConnections.contains(connectionToCheck);
        }
    }

    @Override
    public void accept(DatagramPacket inputPacket) {
        synchronized (virtualConnections) {
            for (VirtualConnection virtualConnection : virtualConnections)
                virtualConnection.handleInput(inputPacket);
        }
    }

    @Override
    public List<DatagramPacket> get() {
        this.removeClosedConnections();
        ArrayList<DatagramPacket> outputPackets = new ArrayList<>();
        synchronized (virtualConnections) {
            for (VirtualConnection virtualConnection : virtualConnections) {
                DatagramPacket outputPacket = virtualConnection.getOutputPacket();
                if (outputPacket != null)
                    outputPackets.add(outputPacket);
            }
            return outputPackets;
        }
    }

    public void closeConnections() {
        synchronized (virtualConnections) {
            for (VirtualConnection virtualConnection : virtualConnections)
                virtualConnection.closeConnection();
            this.removeClosedConnections();
        }
    }

    private void removeClosedConnections() {
        synchronized (virtualConnections) {
            this.closedConnectionRemover.removeClosedConnectionsInList(virtualConnections);
        }
    }

    public class AlreadyHoldingConnectionException extends Exception {
    }

    public class HolderIsFullException extends Exception {
    }

    private static class ManagedVirtualConnection extends VirtualConnection {

        ManagedVirtualConnection(VirtualConnection connection) {
            super(connection);
        }
    }
}
