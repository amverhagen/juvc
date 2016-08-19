package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.VirtualConnection;
import com.andrewverhagen.juvc.holder.ConnectionHolder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class Connector {

    private final ConnectionHolder connectionHolder;
    private DatagramSocket communicationSocket;

    Connector(int maxNumberOfConnections) {
        this.connectionHolder = new ConnectionHolder(maxNumberOfConnections);
    }

    public final void startConnection(VirtualConnection connectionToStart) throws ConnectionHolder.AlreadyHoldingConnectionException, ConnectionHolder.HolderIsFullException {
        this.connectionHolder.addConnection(connectionToStart);
        if (!this.isActive())
            this.startConnector();

    }

    public final boolean isActive() {
        return this.communicationSocket != null && !this.communicationSocket.isClosed();
    }

    public final void close() {
        if (this.communicationSocket != null)
            this.communicationSocket.close();
        this.connectionHolder.closeConnections();
    }

    List<DatagramPacket> getOutputPackets() {
        return this.connectionHolder.getOutputPackets();
    }

    void addInputToConnections(DatagramPacket inputPacket) {
        this.connectionHolder.distributePacketToConnections(inputPacket);
    }

    private void startConnector() {
        close();
        try {
            this.communicationSocket = new DatagramSocket();
            new InputWorker(this, this.communicationSocket).start();
            new OutputWorker(this, this.communicationSocket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
