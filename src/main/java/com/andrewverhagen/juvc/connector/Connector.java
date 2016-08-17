package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.VirtualConnection;
import com.andrewverhagen.juvc.holder.ConnectionHolder;

import java.net.DatagramSocket;
import java.util.Vector;

class Connector {

    private final Vector<VirtualConnection> activeConnections;
    private final int maxNumberOfConnections;
    private DatagramSocket communicationSocket;

    Connector(int maxNumberOfConnections) {
        this.maxNumberOfConnections = maxNumberOfConnections;
        this.activeConnections = new Vector<>();
    }

    void startConnection(VirtualConnection connectionToStart) throws ConnectionAlreadyActiveException, ConnectorIsFullException {
        if (this.connectorIsFull())
            throw new ConnectorIsFullException();
        for (VirtualConnection activeConnection : activeConnections) {
            if (activeConnection.containsAddress(connectionToStart))
                throw new ConnectionAlreadyActiveException();
        }
        this.addNewConnection(connectionToStart);
    }

    private void addNewConnection(VirtualConnection connectionToAdd) {
        this.activeConnections.add(connectionToAdd);
        connectionToAdd.sendOutput();
    }

    private boolean connectorIsFull() {
        return activeConnections.size() >= this.maxNumberOfConnections;
    }

    class ConnectionAlreadyActiveException extends Exception {
    }

    class ConnectorIsFullException extends Exception {

    }
}
