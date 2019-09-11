package com.andrewverhagen.juvc.connector;

import java.net.DatagramSocket;
import java.net.SocketException;

import com.andrewverhagen.juvc.connection.VirtualConnection;
import com.andrewverhagen.juvc.holder.ConnectionPool;

public class Connector {

    private static final int USE_RANDOM_PORT = -1;
    private final ConnectionPool connectionPool;
    private final int localListeningPort;
    private DatagramSocket communicationSocket;

    /**
     * Creates a connector that will use a random local port to listen for
     * connections.
     *
     * @param maxNumberOfConnections
     */
    public Connector(int maxNumberOfConnections) {
        this.connectionPool = new ConnectionPool(maxNumberOfConnections);
        this.localListeningPort = USE_RANDOM_PORT;
    }

    /**
     * Creates a connector with a specified max number of connections and
     *
     * @param maxNumberOfConnections The max number of connections this connector
     *                               can service at anyone given time.
     * @param localListeningPort     A port between 0 and 65535 inclusive. Pass -1
     *                               for any random port.
     */
    public Connector(int maxNumberOfConnections, int localListeningPort) {
        this.connectionPool = new ConnectionPool(maxNumberOfConnections);
        if (localListeningPort < 0 || localListeningPort > 65535)
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        this.localListeningPort = localListeningPort;
    }

    public synchronized final void startConnection(VirtualConnection connectionToStart)
            throws ConnectionPool.AlreadyHoldingConnectionException, ConnectionPool.HolderIsFullException,
            SocketException {
        this.connectionPool.addConnection(connectionToStart);
        if (!this.isActive())
            this.startConnector();
        connectionToStart.openConnection();

    }

    public synchronized final boolean isActive() {
        return this.communicationSocket != null && !this.communicationSocket.isClosed();
    }

    public synchronized final void close() {
        this.connectionPool.closeConnections();
        if (this.communicationSocket != null)
            this.communicationSocket.close();
    }

    private synchronized void startConnector() throws SocketException {
        this.close();
        if (this.localListeningPort == USE_RANDOM_PORT)
            this.communicationSocket = new DatagramSocket();
        else
            this.communicationSocket = new DatagramSocket(localListeningPort);
        new InputWorker(this.connectionPool, this.communicationSocket).start();
        new OutputWorker(this.connectionPool, this.communicationSocket).start();
    }
}
