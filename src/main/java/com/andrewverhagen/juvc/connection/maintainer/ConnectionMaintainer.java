package com.andrewverhagen.juvc.connection.maintainer;

import com.andrewverhagen.juvc.connection.ConnectionState;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Observer;

public class ConnectionMaintainer {

    private final int maxConnections;
    private HashMap<InetSocketAddress, ConnectionState> connections;

    public ConnectionMaintainer(int maxConnections) throws IllegalArgumentException {
        if (maxConnections < 1)
            throw new IllegalArgumentException("maxConnections must be one or greater.");
        this.maxConnections = maxConnections;
        this.connections = new HashMap<>();
    }

    public boolean holdingConnection(InetSocketAddress address) {
        return connections.containsKey(address);
    }

    public boolean addConnections(InetSocketAddress connectionAddress, int connectionTimeOutTimeInMilliSeconds, Observer connectionObserver) throws IllegalArgumentException {
        if (connectionAddress == null)
            throw new IllegalArgumentException("Connection address cannot be null");
        if (connectionTimeOutTimeInMilliSeconds <= 0)
            throw new IllegalArgumentException("Connection timeout time must be greater than 0.");
        if (this.holdingConnection(connectionAddress))
            return false;

        return false;
    }

    public boolean atConnectionCapacity() {
        return maxConnections >= connections.size();
    }

}

