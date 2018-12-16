package com.andrewverhagen.juvc.holder;

import com.andrewverhagen.juvc.connection.DatagramPacketConsumer;
import com.andrewverhagen.juvc.connection.VirtualConnection;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool implements DatagramPacketConsumer, PacketSupplier {

    private final ClosedConnectionRemover closedConnectionRemover;
    private final ArrayList<VirtualConnection> virtualConnections;
    private final int maxAmountOfConnections;

    public ConnectionPool(int maxAmountOfConnections) {
        if (maxAmountOfConnections < 1)
            throw new IllegalArgumentException("Holder must have a max size of at least one.");
        this.maxAmountOfConnections = maxAmountOfConnections;
        this.virtualConnections = new ArrayList<>();
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
            for (VirtualConnection virtualConnection : virtualConnections)
                if (virtualConnection.containsAddress(connectionToCheck))
                    return true;
        }
        return false;
    }

    @Override
    public void accept(DatagramPacket inputPacket) {
        System.out.println("Distributed");
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
}
