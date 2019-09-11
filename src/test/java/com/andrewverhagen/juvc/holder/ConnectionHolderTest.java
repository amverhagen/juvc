package com.andrewverhagen.juvc.holder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

import com.andrewverhagen.juvc.ReceivedPacketInputConsumer;
import com.andrewverhagen.juvc.connection.OutputSupplier;
import com.andrewverhagen.juvc.connection.VirtualConnection;

import org.junit.Test;

public class ConnectionHolderTest {

    private static final Consumer<DatagramPacket> defaultConsumer = new Consumer<DatagramPacket>() {
        @Override
        public void accept(DatagramPacket t) {
        }
    };
    private static OutputSupplier defaultProvider = new OutputSupplier() {

        @Override
        public byte[] get() {
            return new byte[0];
        }
    };

    @Test
    public void ConnectionPool_CreateConnectionPoolWithIllegalArg_ShouldThrownIllegalArgException() {
        boolean illegalArgExceptionThrown = false;
        try {
            new ConnectionPool(0);
        } catch (IllegalArgumentException e) {
            illegalArgExceptionThrown = true;
        }
        assertTrue(illegalArgExceptionThrown);
    }

    @Test
    public void addConnection_AddConnectionToEmptyConnectionPool_ShouldNotThrowAnException() {
        final ConnectionPool testHolder = new ConnectionPool(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full.");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding connection.");
        }
    }

    @Test
    public void addConnection_AddConnectionThatHolderIsAlreadyHolding_ShouldThrowAlreadyHoldingException() {
        final ConnectionPool testHolder = new ConnectionPool(2);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);

        boolean alreadyHoldingThrown = false;
        try {
            testHolder.addConnection(testConnection);
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full.");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            alreadyHoldingThrown = true;
        }
        assertTrue(alreadyHoldingThrown);
    }

    @Test
    public void addConnection_AddConnectionToFullHolder_ShouldThrowHolderFullException() {
        final ConnectionPool testHolder = new ConnectionPool(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final InetSocketAddress secondAddress = new InetSocketAddress(9002);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);
        final VirtualConnection secondTestConnection = new VirtualConnection(secondAddress, 1000, defaultConsumer,
                defaultProvider);

        boolean alreadyHoldingThrown = false;
        try {
            testHolder.addConnection(testConnection);
            testHolder.addConnection(secondTestConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            alreadyHoldingThrown = true;
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding connection.");
        }
        assertTrue(alreadyHoldingThrown);
    }

    @Test
    public void atCapacity_CallAtCapacityOnHolderWithMaxNumberOfConnections_ShouldReturnTrue() {
        final ConnectionPool testHolder = new ConnectionPool(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertTrue(testHolder.atCapacity());
    }

    @Test
    public void atCapacity_CallAtCapacityOnHolderThatIsNotFull_ShouldReturnFalse() {
        final ConnectionPool testHolder = new ConnectionPool(2);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertFalse(testHolder.atCapacity());
    }

    @Test
    public void holdingConnection_CallHoldingConnectionWithConnectionThatHolderContains_ShouldReturnTrue() {
        final ConnectionPool testHolder = new ConnectionPool(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertTrue(testHolder.holdingConnection(testConnection));
    }

    @Test
    public void holdingConnection_CallHoldingConnectionWithConnectionThatHolderDoesNotContain_ShouldReturnFalse() {
        final ConnectionPool testHolder = new ConnectionPool(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final InetSocketAddress differentAddress = new InetSocketAddress(9002);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);
        final VirtualConnection notHeldConnection = new VirtualConnection(differentAddress, 1000, defaultConsumer,
                defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertFalse(testHolder.holdingConnection(notHeldConnection));
    }

    @Test
    public void distributePacketToConnections_DistributePacketWithSameAddressAsConnectionInHolder_ConnectionShouldRecievePacket() {
        final ConnectionPool testHolder = new ConnectionPool(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final ReceivedPacketInputConsumer packetConsumer = new ReceivedPacketInputConsumer();
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, packetConsumer,
                defaultProvider);
        final DatagramPacket testPacket = new DatagramPacket(new byte[0], 0, testAddress);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }

        testConnection.openConnection();
        testHolder.accept(testPacket);

        assertTrue(packetConsumer.receivedData());
    }

    @Test
    public void getOutputPackets_GetPacketsOfClosedHolder_ShouldReturnEmptyList() {
        final ConnectionPool testHolder = new ConnectionPool(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer,
                defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionPool.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionPool.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }

        testConnection.openConnection();
        assertTrue(testHolder.get().size() > 0);

        testHolder.closeConnections();
        assertTrue(testHolder.get().size() == 0);
    }

}