package com.andrewverhagen.juvc.holder;

import com.andrewverhagen.juvc.connection.ConnectionState;
import com.andrewverhagen.juvc.connection.VirtualConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

class ClosedConnectionRemover implements Observer {

    private final ArrayList<VirtualConnection> expiredConnections;

    ClosedConnectionRemover() {
        this.expiredConnections = new ArrayList<>();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof VirtualConnection) {
            if (arg == ConnectionState.CLOSED) {
                synchronized (expiredConnections) {
                    expiredConnections.add((VirtualConnection) o);
                    o.deleteObserver(this);
                }
            }
        } else
            o.deleteObserver(this);
    }

    void removeClosedConnectionsInList(final List<VirtualConnection> virtualConnectionList) {
        synchronized (expiredConnections) {
            virtualConnectionList.removeAll(expiredConnections);
            expiredConnections.clear();
        }
    }
}
