package com.andrewverhagen.juvc.holder;

import com.andrewverhagen.juvc.ReceivedPacketInputConsumer;
import com.andrewverhagen.juvc.connection.InputConsumer;
import com.andrewverhagen.juvc.connection.OutputProvider;
import com.andrewverhagen.juvc.connection.VirtualConnection;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class ConnectionHolderTest {

    private static final InputConsumer defaultConsumer = new InputConsumer() {
        @Override
        public void addDatagramPacket(DatagramPacket inputData) {

        }
    };
    private static OutputProvider defaultProvider = new OutputProvider() {
        @Override
        public byte[] getOutputData() {
            return new byte[0];
        }
    };

    @Test
    public void ConnectionHolder_CreateConnectionHolderWithIllegalArg_ShouldThrownIllegalArgException() {
        boolean illegalArgExceptionThrown = false;
        try {
            new ConnectionHolder(0);
        } catch (IllegalArgumentException e) {
            illegalArgExceptionThrown = true;
        }
        assertTrue(illegalArgExceptionThrown);
    }

    @Test
    public void addConnection_AddConnectionToEmptyConnectionHolder_ShouldNotThrowAnException() {
        final ConnectionHolder testHolder = new ConnectionHolder(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full.");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding connection.");
        }
    }

    @Test
    public void addConnection_AddConnectionThatHolderIsAlreadyHolding_ShouldThrowAlreadyHoldingException() {
        final ConnectionHolder testHolder = new ConnectionHolder(2);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);

        boolean alreadyHoldingThrown = false;
        try {
            testHolder.addConnection(testConnection);
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full.");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            alreadyHoldingThrown = true;
        }
        assertTrue(alreadyHoldingThrown);
    }

    @Test
    public void addConnection_AddConnectionToFullHolder_ShouldThrowHolderFullException() {
        final ConnectionHolder testHolder = new ConnectionHolder(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final InetSocketAddress secondAddress = new InetSocketAddress(9002);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);
        final VirtualConnection secondTestConnection = new VirtualConnection(secondAddress, 1000, defaultConsumer, defaultProvider);

        boolean alreadyHoldingThrown = false;
        try {
            testHolder.addConnection(testConnection);
            testHolder.addConnection(secondTestConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            alreadyHoldingThrown = true;
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding connection.");
        }
        assertTrue(alreadyHoldingThrown);
    }

    @Test
    public void atCapacity_CallAtCapacityOnHolderWithMaxNumberOfConnections_ShouldReturnTrue() {
        final ConnectionHolder testHolder = new ConnectionHolder(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertTrue(testHolder.atCapacity());
    }

    @Test
    public void atCapacity_CallAtCapacityOnHolderThatIsNotFull_ShouldReturnFalse() {
        final ConnectionHolder testHolder = new ConnectionHolder(2);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertFalse(testHolder.atCapacity());
    }

    @Test
    public void holdingConnection_CallHoldingConnectionWithConnectionThatHolderContains_ShouldReturnTrue() {
        final ConnectionHolder testHolder = new ConnectionHolder(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertTrue(testHolder.holdingConnection(testConnection));
    }

    @Test
    public void holdingConnection_CallHoldingConnectionWithConnectionThatHolderDoesNotContain_ShouldReturnFalse() {
        final ConnectionHolder testHolder = new ConnectionHolder(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final InetSocketAddress differentAddress = new InetSocketAddress(9002);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);
        final VirtualConnection notHeldConnection = new VirtualConnection(differentAddress, 1000, defaultConsumer, defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }
        assertFalse(testHolder.holdingConnection(notHeldConnection));
    }

    @Test
    public void distributePacketToConnections_DistributePacketWithSameAddressAsConnectionInHolder_ConnectionShouldRecievePacket() {
        final ConnectionHolder testHolder = new ConnectionHolder(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final ReceivedPacketInputConsumer packetConsumer = new ReceivedPacketInputConsumer();
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, packetConsumer, defaultProvider);
        final DatagramPacket testPacket = new DatagramPacket(new byte[0], 0, testAddress);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }

        testConnection.openConnection();
        testHolder.addDatagramPacket(testPacket);

        assertTrue(packetConsumer.receivedData());
    }

    @Test
    public void getOutputPackets_GetPacketsOfClosedHolder_ShouldReturnEmptyList() {
        final ConnectionHolder testHolder = new ConnectionHolder(1);
        final InetSocketAddress testAddress = new InetSocketAddress(9001);
        final VirtualConnection testConnection = new VirtualConnection(testAddress, 1000, defaultConsumer, defaultProvider);

        try {
            testHolder.addConnection(testConnection);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder should not be full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder should not already be holding address");
        }

        testConnection.openConnection();
        assertTrue(testHolder.getOutputPackets().size() > 0);

        testHolder.closeConnections();
        assertTrue(testHolder.getOutputPackets().size() == 0);
    }


}