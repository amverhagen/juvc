package com.andrewverhagen.juvc;

import com.andrewverhagen.juvc.connection.ConnectionState;
import org.junit.Assert;

import java.util.Observable;
import java.util.Observer;

public class ConnectionStateTester implements Observer {

    private ConnectionState connectionState;

    @Override
    public void update(Observable o, Object arg) {
        try {
            connectionState = (ConnectionState) arg;
        } catch (ClassCastException e) {
            Assert.fail("State test came across invalid cast.");
        }
    }

    public void testObserverIsInState(ConnectionState expectedState) {
        Assert.assertNotNull(connectionState);
        Assert.assertNotNull(expectedState);
        Assert.assertEquals(expectedState, connectionState);
    }
}
