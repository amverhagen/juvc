package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.ConnectionState;
import com.andrewverhagen.juvc.connection.InputHandler;
import com.andrewverhagen.juvc.connection.OutputSender;
import com.andrewverhagen.juvc.connection.VirtualConnection;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.*;

public class ConnectorTest {

    private static InetSocketAddress localHost;
    private static InputHandler alwaysTrueHandler;
    private static OutputSender defaultOutputSender;

    @BeforeClass
    public static void initCommonObjects() {
        localHost = new InetSocketAddress(1000);
        alwaysTrueHandler = new InputHandler() {
            @Override
            public boolean handleInput(byte[] incomingData) {
                return true;
            }
        };
        defaultOutputSender = new OutputSender() {
            @Override
            public void sendOutput() {
            }
        };
    }

    @Test
    public void startConnection_TwoConnectionsWithTheSameAddress_ThrowsConnectionAlreadyActiveException() {
        VirtualConnection connection = new VirtualConnection(localHost, 2000, alwaysTrueHandler, defaultOutputSender);
        VirtualConnection connectionWithSameAddress = new VirtualConnection(localHost, 2000, alwaysTrueHandler, defaultOutputSender);
        Connector connector = new Connector();
        boolean alreadyActiveExceptionThrown = false;
        try {
            connector.startConnection(connection);
        } catch (Connector.ConnectionAlreadyActiveException e) {
            alreadyActiveExceptionThrown = true;
        }
        assertFalse(alreadyActiveExceptionThrown);
        try {
            connector.startConnection(connectionWithSameAddress);
        } catch (Connector.ConnectionAlreadyActiveException e) {
            alreadyActiveExceptionThrown = true;
        }
        assertTrue(alreadyActiveExceptionThrown);
    }

    @Test
    public void startConnection_GivenUnopenedConnection_PutsConnectionIntoConnectingState() {
        VirtualConnection unopenedConnection = new VirtualConnection(localHost, 2000, alwaysTrueHandler, defaultOutputSender);
        ConnectionStateTester observerTester = new ConnectionStateTester();
        unopenedConnection.addObserver(observerTester);
        Connector connector = new Connector();
        try {
            connector.startConnection(unopenedConnection);
        } catch (Connector.ConnectionAlreadyActiveException e) {
            e.printStackTrace();
        }
        observerTester.testObserverIsInState(ConnectionState.CONNECTING);
    }

    private class ConnectionStateTester implements Observer {

        private ConnectionState connectionState;

        @Override
        public void update(Observable o, Object arg) {
            try {
                connectionState = (ConnectionState) arg;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        private void testObserverIsInState(ConnectionState expectedState) {
            assertNotNull(connectionState);
            assertNotNull(expectedState);
            assertEquals(expectedState, connectionState);
        }
    }
}