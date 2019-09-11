package com.andrewverhagen.juvc.connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

import com.andrewverhagen.juvc.ConnectionStateTester;

import org.junit.BeforeClass;
import org.junit.Test;

public class VirtualConnectionTest {

    private static Consumer<DatagramPacket> defaultInputConsumer;
    private static OutputSupplier defaultOutputSupplier;

    private static final Consumer<DatagramPacket> FAIL_ON_INPUT_CONSUMER = new Consumer<DatagramPacket>() {

        @Override
        public void accept(DatagramPacket t) {
            fail("Input should not have been passed to this consumer.");
        }
    };

    @BeforeClass
    public static void initHandlers() {

        defaultInputConsumer = new Consumer<DatagramPacket>() {
            @Override
            public void accept(DatagramPacket t) {

            }
        };

        defaultOutputSupplier = new OutputSupplier() {

            @Override
            public byte[] get() {
                return new byte[0];
            }
        };
    };

    @Test
    public void handleInput_GivenInputWhileConnectionIsNotActive_ShouldNotBePassedToInputConsumer() {
        final InetSocketAddress testAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, FAIL_ON_INPUT_CONSUMER,
                defaultOutputSupplier);
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, testAddress));
    }

    @Test
    public void handleInput_GivenInputWithDifferentAddress_ShouldNotBePassedToInputConsumer() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, FAIL_ON_INPUT_CONSUMER,
                defaultOutputSupplier);

        testConnection.openConnection();

        final InetSocketAddress differingAddress = new InetSocketAddress(9001);
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, differingAddress));
    }

    @Test
    public void handleInput_HandleInputCalledOnConnectingConnection_ShouldPutConnectionInConnectedState() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();

        testConnection.addObserver(connectionStateTester);
        testConnection.openConnection();
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, connectionAddress));

        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTED);
    }

    @Test
    public void handleInput_HandleInputCalledWithData_InputConsumerShouldReceiveSameData() {
        final byte[] dataToSend = { (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final DataCheckingInputConsumer checkingInputConsumer = new DataCheckingInputConsumer();
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, checkingInputConsumer,
                defaultOutputSupplier);

        testConnection.openConnection();
        testConnection.handleInput(new DatagramPacket(dataToSend, dataToSend.length, connectionAddress));

        checkingInputConsumer.addedDataMatchesData(dataToSend);
    }

    @Test
    public void getOutputPacket_GetOutputPacketOfUnopenedConnection_ShouldReturnNull() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);

        assertNull(testConnection.getOutputPacket());
    }

    @Test
    public void getOutputPacket_GetOutputPacketOfActiveConnection_ShouldReturnExpectedOutputPacket() {
        final byte[] correctOutput = { (byte) 10, (byte) 9, (byte) 8 };
        final OutputSupplier outputProvider = new OutputSupplier() {

            @Override
            public byte[] get() {
                return correctOutput;
            }
        };
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                outputProvider);
        testConnection.openConnection();
        DatagramPacket outputPacket = testConnection.getOutputPacket();

        assertNotNull(outputPacket);
        assertTrue(outputPacket.getData() == correctOutput);
    }

    @Test
    public void openConnection_OpeningAnUnopenedConnection_ShouldPutConnectionInConnectingState() {
        final InetSocketAddress testAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 2000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();

        testConnection.addObserver(connectionStateTester);
        testConnection.openConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTING);
    }

    @Test
    public void openConnection_OpeningAConnectingConnection_ShouldLeaveConnectionInConnectingState() {
        final InetSocketAddress testAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 2000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();

        testConnection.addObserver(connectionStateTester);
        testConnection.openConnection();
        testConnection.openConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTING);
    }

    @Test
    public void openConnection_OpenConnectionOnAConnectedConnection_ShouldLeaveConnectionInConnected() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();

        testConnection.addObserver(connectionStateTester);
        testConnection.openConnection();
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, connectionAddress));

        testConnection.openConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTED);
    }

    @Test
    public void openConnection_OpenConnectionOnAClosedConnection_ShouldLeaveConnectionInClosed() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();

        testConnection.addObserver(connectionStateTester);
        testConnection.closeConnection();

        testConnection.openConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CLOSED);
    }

    @Test
    public void closeConnection_CloseUnopenedConnection_ShouldPutConnectionInClosedState() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();
        testConnection.addObserver(connectionStateTester);

        testConnection.closeConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CLOSED);
    }

    @Test
    public void closeConnection_CloseConnectingConnection_ShouldPutConnectionInClosedState() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();
        testConnection.addObserver(connectionStateTester);

        testConnection.openConnection();
        testConnection.closeConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CLOSED);
    }

    @Test
    public void closeConnection_CloseConnectedConnection_ShouldPutConnectionInClosedState() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();
        testConnection.addObserver(connectionStateTester);

        testConnection.openConnection();
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, connectionAddress));
        testConnection.closeConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CLOSED);
    }

    @Test
    public void closeConnection_CloseClosedConnection_ShouldLeaveConnectionInClosedState() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();
        testConnection.addObserver(connectionStateTester);

        testConnection.openConnection();
        testConnection.closeConnection();

        connectionStateTester.testObserverIsInState(ConnectionState.CLOSED);

        testConnection.closeConnection();
        connectionStateTester.testObserverIsInState(ConnectionState.CLOSED);
    }

    @Test
    public void handleInput_SendConnectionInputAfterTimeoutTimeHasElapsed_ShouldPutConnectionIntoClosedState()
            throws InterruptedException {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 1, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();
        testConnection.addObserver(connectionStateTester);

        testConnection.openConnection();
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, connectionAddress));
        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTED);

        Thread.sleep(10);
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, connectionAddress));
        connectionStateTester.testObserverIsInState(ConnectionState.CLOSED);
    }

    @Test
    public void handleInput_SendConnectionInputBeforeTimeoutHasElapsed_ConnectionShouldBeInConnectedState()
            throws InterruptedException {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnection = new VirtualConnection(connectionAddress, 5000, defaultInputConsumer,
                defaultOutputSupplier);
        final ConnectionStateTester connectionStateTester = new ConnectionStateTester();
        testConnection.addObserver(connectionStateTester);

        testConnection.openConnection();

        Thread.sleep(100);
        testConnection.handleInput(new DatagramPacket(new byte[0], 0, connectionAddress));
        connectionStateTester.testObserverIsInState(ConnectionState.CONNECTED);
    }

    @Test
    public void holdingConnection_TestConnectionHoldsAddressOfDifferentConnectionWithSameAddress_ShouldReturnTrue() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final VirtualConnection testConnectionOne = new VirtualConnection(connectionAddress, 1, defaultInputConsumer,
                defaultOutputSupplier);
        final VirtualConnection testConnectionTwo = new VirtualConnection(connectionAddress, 1, defaultInputConsumer,
                defaultOutputSupplier);

        assertTrue(testConnectionOne.equals(testConnectionTwo));
    }

    @Test
    public void holdingConnection_TestConnectionHoldsAddressOfDifferentConnectionWithDifferentAddress_ShouldReturnFalse() {
        final InetSocketAddress connectionAddress = new InetSocketAddress(9000);
        final InetSocketAddress differentAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnectionOne = new VirtualConnection(connectionAddress, 1, defaultInputConsumer,
                defaultOutputSupplier);
        final VirtualConnection testConnectionTwo = new VirtualConnection(differentAddress, 1, defaultInputConsumer,
                defaultOutputSupplier);

        assertFalse(testConnectionOne.equals(testConnectionTwo));
    }

    private class DataCheckingInputConsumer implements Consumer<DatagramPacket> {
        private byte[] addedData;

        public void addedDataMatchesData(byte[] dataToCheckAgainst) {
            assertNotNull(this.addedData);
            assertTrue(this.addedData == dataToCheckAgainst);
        }

        @Override
        public void accept(DatagramPacket inputData) {
            this.addedData = inputData.getData();
        }
    }
}