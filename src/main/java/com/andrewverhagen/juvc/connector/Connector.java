package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.VirtualConnection;

import java.net.DatagramSocket;
import java.util.Vector;

public class Connector {

    private final Vector<VirtualConnection> activeConnections;
    private DatagramSocket communicationSocket;

    public Connector() {
        this.activeConnections = new Vector<>();
    }

    public void startConnection(VirtualConnection connectionToStart) throws ConnectionAlreadyActiveException {
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

    public class ConnectionAlreadyActiveException extends Exception {
    }
}
