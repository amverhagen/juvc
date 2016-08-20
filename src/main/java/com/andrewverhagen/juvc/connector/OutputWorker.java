package com.andrewverhagen.juvc.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class OutputWorker extends Thread {

    private final DatagramSocket outputSocket;
    private final Connector connector;

    OutputWorker(Connector connector, DatagramSocket outputSocket) {
        this.connector = connector;
        this.outputSocket = outputSocket;
    }

    @Override
    public void run() {
        while (!outputSocket.isClosed()) {
            for (DatagramPacket outputPacket : this.connector.getOutputPackets()) {
                try {
                    outputSocket.send(outputPacket);
                } catch (IOException e) {

                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }
    }

}
