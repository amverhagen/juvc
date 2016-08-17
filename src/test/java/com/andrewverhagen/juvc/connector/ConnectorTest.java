package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.ConnectionState;
import com.andrewverhagen.juvc.connection.InputHandler;
import com.andrewverhagen.juvc.connection.OutputSender;
import com.andrewverhagen.juvc.connection.VirtualConnection;
import com.andrewverhagen.juvc.holder.ConnectionHolder;
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
    public void startConnection_StartConnectionThatConnectorDoesNotHave_ConnectionGoesIntoConnectingState() {
        Connector connector = new Connector(1);
        VirtualConnection testConnection = new VirtualConnection(localHost, 100, alwaysTrueHandler, defaultOutputSender);
        ConnectionStateTester stateTester = new ConnectionStateTester();
        testConnection.addObserver(stateTester);

        try {
            connector.startConnection(testConnection);
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Connector should not already be holding connection.");
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Connector should not be full");
        }

        stateTester.testObserverIsInState(ConnectionState.CONNECTING);
    }

    @Test
    public void startConnection_StartConnectionThatConnectorAlreadyHas_ShouldThrowAlreadyHoldingException() {
        Connector connector = new Connector(2);
        VirtualConnection testConnection = new VirtualConnection(localHost, 100, alwaysTrueHandler, defaultOutputSender);
        try {
            connector.startConnection(testConnection);
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Connector should not already be holding connection.");
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Connector should not be full");
        }
        boolean alreadyHoldingThrown = false;
        try {
            connector.startConnection(testConnection);
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            alreadyHoldingThrown = true;
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Connector should not be full");
        }
        assertTrue(alreadyHoldingThrown);
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