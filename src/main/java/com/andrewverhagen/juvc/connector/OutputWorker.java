package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.holder.PacketProvider;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class OutputWorker extends Thread {

    private final DatagramSocket outputSocket;
    private final PacketProvider packetProvider;

    OutputWorker(PacketProvider packetProvider, DatagramSocket outputSocket) {
        this.outputSocket = outputSocket;
        this.packetProvider = packetProvider;
    }

    @Override
    public void run() {
        while (!outputSocket.isClosed()) {
            for (DatagramPacket outputPacket : this.packetProvider.getPackets()) {
                try {
                    outputSocket.send(outputPacket);
                } catch (IOException e) {
                    outputSocket.close();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                outputSocket.close();
            }
        }
    }

}
