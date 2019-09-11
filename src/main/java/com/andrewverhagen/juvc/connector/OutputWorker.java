package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.holder.DatagramPacketSupplier;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class OutputWorker extends Thread {

    private final DatagramSocket outputSocket;
    private final DatagramPacketSupplier packetSupplier;

    OutputWorker(DatagramPacketSupplier packetProvider, DatagramSocket outputSocket) {
        this.outputSocket = outputSocket;
        this.packetSupplier = packetProvider;
    }

    @Override
    public void run() {
        while (!outputSocket.isClosed()) {
            for (DatagramPacket outputPacket : this.packetSupplier.get()) {
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
