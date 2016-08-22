package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.holder.PacketProvider;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class OutputWorkerTest {

    @Test
    public void start_MakeOutputWorkerSendPacketsToSocket_SocketShouldReceiveAPacket() throws IOException {
        final InetAddress localHost = InetAddress.getLocalHost();
        final DatagramSocket connectionSocket = new DatagramSocket();
        final PacketProvider packetProvider = new PacketProvider() {
            @Override
            public List<DatagramPacket> getPackets() {
                ArrayList<DatagramPacket> packets = new ArrayList<>();
                DatagramPacket outputPacket = new DatagramPacket(new byte[0], 0);
                outputPacket.setAddress(localHost);
                outputPacket.setPort(connectionSocket.getLocalPort());
                packets.add(outputPacket);
                return packets;
            }
        };
        final OutputWorker outputWorker = new OutputWorker(packetProvider, connectionSocket);
        assertFalse(connectionSocket.isClosed());
        outputWorker.start();
        connectionSocket.setSoTimeout(100);
        try {
            connectionSocket.receive(new DatagramPacket(new byte[0], 0));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Socket should not throw exception.");
        } finally {
            connectionSocket.close();
        }
    }
}
