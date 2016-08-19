package com.andrewverhagen.juvc.holder;

import com.andrewverhagen.juvc.connection.InputConsumer;
import com.andrewverhagen.juvc.connection.OutputProvider;
import com.andrewverhagen.juvc.connection.VirtualConnection;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class ConnectionHolderTest {

    private static InputConsumer inputConsumer;
    private static OutputProvider outputProvider;
    private static VirtualConnection testConnection9001;
    private static VirtualConnection testConnection9002;
    private static ConnectionHolder testHolder;


    @BeforeClass
    public static void initSharedObjects() {
        initHandlers();
        InetSocketAddress localHostPort9001 = new InetSocketAddress(9001);
        InetSocketAddress localHostPort9002 = new InetSocketAddress(9002);

        testConnection9001 = new VirtualConnection(localHostPort9001, 2000, inputConsumer, outputProvider);
        testConnection9002 = new VirtualConnection(localHostPort9002, 2000, inputConsumer, outputProvider);
        testHolder = new ConnectionHolder(1);
        try {
            testHolder.addConnection(testConnection9001);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder is full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder is already holding connection");
        }
    }

    private static void initHandlers() {
        InputConsumer inputConsumer = new InputConsumer() {
            @Override
            public void addInputData(byte[] inputData) {

            }
        };
        OutputProvider outputProvider = new OutputProvider() {
            @Override
            public byte[] getOutputData() {
                return new byte[0];
            }
        };
    }

    @Test
    public void initialization_GiveMaxConnectionsLessThanOne_ThrowsIllegalArgException() {
        boolean illegalArgExceptionThrown = false;
        try {
            new ConnectionHolder(0);
        } catch (IllegalArgumentException e) {
            illegalArgExceptionThrown = true;
        }
        assertTrue(illegalArgExceptionThrown);
    }

    @Test
    public void testThatHolderContainsConnectionReturnsTrue() {
        assertTrue(testHolder.holdingConnection(testConnection9001));
    }

    @Test
    public void testThatHolderIsAtCapacity() {
        assertTrue(testHolder.atCapacity());
    }

    @Test
    public void addConnection_GiveHolderConnectionWhenFull_HolderThrowsAtCapacityException() {
        boolean atCapacityThrown = false;
        try {
            testHolder.addConnection(testConnection9002);
        } catch (ConnectionHolder.HolderIsFullException e) {
            atCapacityThrown = true;
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            fail("Holder is already holding connection");
        }
        assertTrue(atCapacityThrown);
    }

    @Test
    public void addConnection_GiveHolderSameConnection_ThrowsAlreadyHoldingConnectionException() {
        boolean exceptionThrown = false;
        try {
            testHolder.addConnection(testConnection9001);
        } catch (ConnectionHolder.HolderIsFullException e) {
            fail("Holder is already full");
        } catch (ConnectionHolder.AlreadyHoldingConnectionException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

}