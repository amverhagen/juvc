package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.InputConsumer;
import com.andrewverhagen.juvc.connection.OutputProvider;
import com.andrewverhagen.juvc.connection.VirtualConnection;
import com.andrewverhagen.juvc.holder.ConnectionHolder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConnectorTest {

    private static InputConsumer defaultInputConsumer;
    private static OutputProvider defaultOutputProvider;

    @BeforeClass
    public static void initCommonObjects() {
        defaultInputConsumer = new InputConsumer() {
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

    @Test
    public void connector_InitConnectorWithMaxConnectionSizeLessThanOne_ShouldThrowException() {
        boolean illegalArgThrown = false;
        try {
            Connector testConnector = new Connector(0);
        } catch (IllegalArgumentException e) {
            illegalArgThrown = true;
        }
        assertTrue(illegalArgThrown);
    }

    @Test
    public void connector_InitConnectorWithOutOfBoundsPortNumbers_ShouldThrowException() {
        boolean illegalArgThrown = false;
        try {
            new Connector(1, -1);
        } catch (IllegalArgumentException e) {
            illegalArgThrown = true;
        }
        assertTrue(illegalArgThrown);

        illegalArgThrown = false;
        try {
            new Connector(1, 66000);
        } catch (IllegalArgumentException e) {
            illegalArgThrown = true;
        }
        assertTrue(illegalArgThrown);
    }

    @Test
    public void connector_InitWithValidParams_ShouldNotThrowException() {
        new Connector(1, 1);
    }

    @Test
    public void startConnection_PassValidConnectionToStartConnection_ShouldStartConnector() {
        final InetSocketAddress testAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultInputConsumer, defaultOutputProvider);
        final Connector testConnector = new Connector(1);
        try {
            testConnector.startConnection(testConnection);
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Connector should not already have connection.");
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Connector should not be full.");
        } catch (SocketException e) {
            fail("Connector unable to open socket.");
        }
        assertTrue(testConnector.isActive());
        testConnector.close();
    }

    @Test
    public void startConnection_StartConnectionWhilePortIsAlreadyUsed_ShouldThrowSocketException() {
        final InetSocketAddress testAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultInputConsumer, defaultOutputProvider);
        final Connector testConnector = new Connector(1, 10);
        DatagramSocket testSocket;
        boolean socketExceptionThrown = false;
        try {
            testSocket = new DatagramSocket(10);
            try {
                testConnector.startConnection(testConnection);
            } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
                fail("Connector should not already have connection.");
            } catch (ConnectionHolder.HolderIsFullException e) {
                fail("Connector should not be full.");
            } catch (SocketException e) {
                socketExceptionThrown = true;
            }
            testConnector.close();
            testSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        assertTrue(socketExceptionThrown);
    }

    @Test
    public void closeConnection_CloseActiveConnection_ShouldPutConnectionStateOfVirtualConnectionToClosed() {
        final InetSocketAddress testAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultInputConsumer, defaultOutputProvider);
        final Connector testConnector = new Connector(1);
        try {
            testConnector.startConnection(testConnection);
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Connector should not already have connection.");
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Connector should not be full.");
        } catch (SocketException e) {
            fail("Connector unable to open socket.");
        }
        assertTrue(testConnector.isActive());
        testConnector.close();
        assertTrue(!testConnector.isActive());
    }
}