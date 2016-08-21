package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.ReceivedPacketInputConsumer;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.Assert.assertTrue;

public class InputWorkerTest {

    @Test
    public void start_SendPacketToAddressInputWorkerIsListeningOn_VirtualConnectionShouldReceiveInput() throws IOException, InterruptedException {
        final DatagramSocket connectionSocket = new DatagramSocket();

        final ReceivedPacketInputConsumer receivedPacketInputConsumer = new ReceivedPacketInputConsumer();
        final InputWorker testInputWorker = new InputWorker(receivedPacketInputConsumer, connectionSocket);

        final DatagramPacket datagramPacket = new DatagramPacket(new byte[0], 0);
        datagramPacket.setAddress(InetAddress.getLocalHost());
        datagramPacket.setPort(connectionSocket.getLocalPort());
        connectionSocket.send(datagramPacket);

        testInputWorker.start();

        Thread.sleep(10);

        assertTrue(receivedPacketInputConsumer.receivedData());
        connectionSocket.close();
    }

}
