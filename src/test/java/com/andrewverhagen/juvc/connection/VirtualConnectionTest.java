package com.andrewverhagen.juvc.connection;

import org.junit.BeforeClass;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VirtualConnectionTest {

    private static InetSocketAddress localHostSocketAddress;
    private static InputHandler alwaysTrueInputHandler;
    private static OutputSender defaultOutputSender;
    private static DatagramPacket testPacket;

    @BeforeClass
    public static void initHandlers() {
        alwaysTrueInputHandler = new InputHandler() {
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
        localHostSocketAddress = new InetSocketAddress(9001);
        testPacket = new DatagramPacket(new byte[0], 0);
    }

    @Test
    public void sendOutput_SendOutputWhileStateIsUnopened_ShouldChangeStateToConnecting() {
        class ConnectingConnectionObserver implements Observer {
            public boolean connectionWasSetToConnecting = false;

            @Override
            public void update(Observable o, Object arg) {
                try {
                    ConnectionState newState = (ConnectionState) arg;
                    if (newState == ConnectionState.CONNECTING)
                        connectionWasSetToConnecting = true;
                    else
                        connectionWasSetToConnecting = false;
                } catch (ClassCastException e) {
                    connectionWasSetToConnecting = false;
                }
            }
        }
        ConnectingConnectionObserver connectionObserver = new ConnectingConnectionObserver();
        final VirtualConnection testConnection = new VirtualConnection(localHostSocketAddress, 2000, alwaysTrueInputHandler, defaultOutputSender);
        testConnection.addObserver(connectionObserver);
        testConnection.sendOutput();
        assertTrue(connectionObserver.connectionWasSetToConnecting);
    }


    @Test
    public void handleInput() throws Exception {
        testPacket.setSocketAddress(localHostSocketAddress);
        final VirtualConnection connection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputHandler, defaultOutputSender);
        assertEquals(true, connection.handleInput(testPacket));

        InetSocketAddress wrongPortAddress = new InetSocketAddress(9002);
        testPacket.setSocketAddress(wrongPortAddress);
        assertEquals(false, connection.handleInput(testPacket));
    }

    @Test
    public void containsAddress() throws Exception {
        testPacket.setSocketAddress(localHostSocketAddress);
        final VirtualConnection connection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputHandler, defaultOutputSender);
        assertTrue(connection.containsAddress(testPacket.getSocketAddress()));
    }

    @Test
    public void containsAddress1() throws Exception {
        final VirtualConnection connection = new VirtualConnection(localHostSocketAddress, 1000, alwaysTrueInputHandler, defaultOutputSender);
        assertTrue(connection.containsAddress(localHostSocketAddress));
    }

    @Test
    public void isEnded() throws Exception {
        final int connectionTimeoutTime = 1;
        VirtualConnection connection = new VirtualConnection(localHostSocketAddress, connectionTimeoutTime, alwaysTrueInputHandler, defaultOutputSender);
        assertEquals(false, connection.isEnded());
        connection.sendOutput();
        Thread.sleep(connectionTimeoutTime * 2);
        assertEquals(true, connection.isEnded());
    }

}