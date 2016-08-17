package com.andrewverhagen.juvc.holder;

import com.andrewverhagen.juvc.connection.InputHandler;
import com.andrewverhagen.juvc.connection.OutputSender;
import com.andrewverhagen.juvc.connection.VirtualConnection;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class ConnectionHolderTest {

    private static InputHandler inputHandler;
    private static OutputSender outputSender;
    private static VirtualConnection testConnection9001;
    private static VirtualConnection testConnection9002;
    private static ConnectionHolder testHolder;


    @BeforeClass
    public static void initSharedObjects() {
        initHandlers();
        InetSocketAddress localHostPort9001 = new InetSocketAddress(9001);
        InetSocketAddress localHostPort9002 = new InetSocketAddress(9002);

        testConnection9001 = new VirtualConnection(localHostPort9001, 2000, inputHandler, outputSender);
        testConnection9002 = new VirtualConnection(localHostPort9002, 2000, inputHandler, outputSender);
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
        InputHandler inputHandler = new InputHandler() {
            @Override
            public boolean handleInput(byte[] incomingData) {
                return true;
            }
        };
        OutputSender outputSender = new OutputSender() {
            @Override
            public void sendOutput() {

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