package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.holder.ConnectionHolder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class OutputWorker extends Thread {

    private final DatagramSocket outputSocket;
    private final ConnectionHolder connectionHolder;

    OutputWorker(ConnectionHolder connectionHolder, DatagramSocket outputSocket) {
        this.connectionHolder = connectionHolder;
        this.outputSocket = outputSocket;
    }

    @Override
    public void run() {
        while (!outputSocket.isClosed()) {
            for (DatagramPacket outputPacket : this.connectionHolder.getOutputPackets()) {
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
