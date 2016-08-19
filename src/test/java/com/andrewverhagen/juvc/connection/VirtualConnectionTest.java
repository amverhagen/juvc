package com.andrewverhagen.juvc.connection;

import com.andrewverhagen.juvc.ConnectionStateTester;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertTrue;

public class VirtualConnectionTest {

    private static InetSocketAddress localHostSocketAddress;
    private static InputConsumer alwaysTrueInputConsumer;
    private static OutputProvider defaultOutputProvider;

    @BeforeClass
    public static void initHandlers() {
        alwaysTrueInputConsumer = new InputConsumer() {
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
        localHostSocketAddress = new InetSocketAddress(9001);
    }

    @Test
    public void sendOutput_SendOutputWhileStateIsUnopened_ShouldChangeStateToConnecting() {
        final VirtualConnection testConnection = new VirtualConnection(localHostSocketAddress, 2000, alwaysTrueInputConsumer, defaultOutputProvider);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();

        testConnection.addObserver(connectionStateTester);
        testConnection.openConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTING);
    }

    @Test
    public void handleInput_GivenValidInputData_ShouldChangeConnectionToConnected() {
        final VirtualConnection testConnection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputConsumer, defaultOutputProvider);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();
        final DatagramPacket inputPacket = new DatagramPacket(new byte[0], 0, localHostSocketAddress);

        testConnection.addObserver(connectionStateTester);
        testConnection.openConnection();
        testConnection.handleInput(inputPacket);

        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTED);
    }

    @Test
    public void containsAddress_GivenSocketAddressOfDatagramPacketWithSameAddressAsConnection_ShouldReturnTrue() throws Exception {
        final DatagramPacket testPacket = new DatagramPacket(new byte[0], 0, localHostSocketAddress);
        final VirtualConnection connection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputConsumer, defaultOutputProvider);
        assertTrue(connection.containsAddress(testPacket.getSocketAddress()));
    }

    @Test
    public void containsAddress_GivenCopyOfSameConnection_ShouldReturnTrue() {
        VirtualConnection testConnection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputConsumer, defaultOutputProvider);
        assertTrue(testConnection.containsAddress(testConnection));
    }

    @Test
    public void containsAddress_GivenCopyOfAddressConnectIsStartedWith_ShouldReturnTrue() {
        final VirtualConnection connection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputConsumer, defaultOutputProvider);
        assertTrue(connection.containsAddress(localHostSocketAddress));
    }

    @Test
    public void closeConnection_ClosingConnectionPutsObserversInCorrectState_ShouldReturnTrue() {
        final VirtualConnection testConnection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputConsumer, defaultOutputProvider);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();

        testConnection.addObserver(connectionStateTester);
        testConnection.closeConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.ENDED);
    }
}