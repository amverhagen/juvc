package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.ConnectionStateTester;
import com.andrewverhagen.juvc.connection.ConnectionState;
import com.andrewverhagen.juvc.connection.InputConsumer;
import com.andrewverhagen.juvc.connection.OutputProvider;
import com.andrewverhagen.juvc.connection.VirtualConnection;
import com.andrewverhagen.juvc.holder.ConnectionHolder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConnectorTest {

    private static InetSocketAddress localHost;
    private static InputConsumer alwaysTrueHandler;
    private static OutputProvider defaultOutputProvider;

    @BeforeClass
    public static void initCommonObjects() {
        localHost = new InetSocketAddress(1000);
        alwaysTrueHandler = new InputConsumer() {
            @Override
            public void addInputData(byte[] inputData) {

            }
        };
        defaultOutputProvider = new OutputProvider() {
            @Override
            public byte[] getOutputData() {
                return new byte[0];
            }
        };
    }

//    @Test
//    public void startConnection_StartConnectionThatConnectorDoesNotHave_ConnectionGoesIntoConnectingState() {
//        Connector connector = new Connector(1);
//        VirtualConnection testConnection = new VirtualConnection(localHost, 100, alwaysTrueHandler, defaultOutputProvider);
//        ConnectionStateTester stateTester = new ConnectionStateTester();
//        testConnection.addObserver(stateTester);
//
//        try {
//            connector.startConnection(testConnection);
//        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
//            fail("Connector should not already be holding connection.");
//        } catch (ConnectionHolder.HolderIsFullException e) {
//            fail("Connector should not be full");
//        }
//
//        stateTester.testObserverIsInState(ConnectionState.CONNECTING);
//        connector.close();
//    }
//
//    @Test
//    public void startConnection_StartConnectionThatConnectorAlreadyHas_ShouldThrowAlreadyHoldingException() {
//        Connector connector = new Connector(2);
//        VirtualConnection testConnection = new VirtualConnection(localHost, 100, alwaysTrueHandler, defaultOutputProvider);
//        try {
//            connector.startConnection(testConnection);
//        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
//            fail("Connector should not already be holding connection.");
//        } catch (ConnectionHolder.HolderIsFullException e) {
//            fail("Connector should not be full");
//        }
//        boolean alreadyHoldingThrown = false;
//        try {
//            connector.startConnection(testConnection);
//        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
//            alreadyHoldingThrown = true;
//        } catch (ConnectionHolder.HolderIsFullException e) {
//            fail("Connector should not be full");
//        }
//        assertTrue(alreadyHoldingThrown);
//        connector.close();
//    }

//    @Test
//    public void startConnection_StartConnectionOnFullConnector_ShouldThrowHolderFullException() {
//        Connector connector = new Connector(1);
//        VirtualConnection testConnection = new VirtualConnection(localHost, 100, alwaysTrueHandler, defaultOutputProvider);
//        InetSocketAddress secondAddress = new InetSocketAddress(9002);
//        VirtualConnection secondConnection = new VirtualConnection(secondAddress, 100, alwaysTrueHandler, defaultOutputProvider);
//        try {
//            connector.startConnection(testConnection);
//        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
//            fail("Connector should not already be holding connection.");
//        } catch (ConnectionHolder.HolderIsFullException e) {
//            fail("Connector should not be full");
//        }
//        boolean isFullThrown = false;
//        try {
//            connector.startConnection(secondConnection);
//        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
//            fail("Connector should not already hold second address");
//        } catch (ConnectionHolder.HolderIsFullException e) {
//            isFullThrown = true;
//        }
//        assertTrue(isFullThrown);
//        connector.close();
//    }

//    @Test
//    public void isActive_StartAValidConnection_ShouldReturnTrue() {
//        Connector connector = new Connector(1);
//        try {
//            connector.startConnection(new VirtualConnection(localHost, 100, alwaysTrueHandler, defaultOutputProvider));
//        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
//            fail("Connector should not already be holding connection.");
//        } catch (ConnectionHolder.HolderIsFullException e) {
//            fail("Connector should not be full.");
//        }
//        assertTrue(connector.isActive());
//        connector.close();
//    }
}