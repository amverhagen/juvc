package com.andrewverhagen.juvc.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class InputWorker extends Thread {

    private final DatagramSocket inputSocket;
    private final DatagramPacket inputPacket;
    private final Connector connector;

    public InputWorker(Connector connector, DatagramSocket inputSocket) {
        this.inputSocket = inputSocket;
        this.inputPacket = new DatagramPacket(new byte[256], 256);
        this.connector = connector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                inputSocket.receive(this.inputPacket);
                connector.addInputToConnections(this.inputPacket);
            } catch (IOException e) {
                inputSocket.close();
            }
        }
    }
}
