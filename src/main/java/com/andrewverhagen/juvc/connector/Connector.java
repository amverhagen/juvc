package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.VirtualConnection;
import com.andrewverhagen.juvc.holder.ConnectionHolder;

import java.net.DatagramSocket;

class Connector {

    private ConnectionHolder connectionHolder;
    private DatagramSocket communicationSocket;

    Connector(int maxNumberOfConnections) {
        this.connectionHolder = new ConnectionHolder(maxNumberOfConnections);
    }

    public void startConnection(VirtualConnection connectionToStart) throws ConnectionHolder.AlreadyHoldingConnectionException, ConnectionHolder.HolderIsFullException {
        if (!this.connectionHolder.holdingConnection(connectionToStart))
            connectionToStart.sendOutput();
        this.connectionHolder.addConnection(connectionToStart);
        connectionToStart.sendOutput();
    }

}
