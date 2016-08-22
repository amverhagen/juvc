package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.connection.InputConsumer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class InputWorker extends Thread {

    private final DatagramSocket inputSocket;
    private final DatagramPacket inputPacket;
    private final InputConsumer inputConsumer;

    InputWorker(InputConsumer inputConsumer, DatagramSocket inputSocket) {
        this.inputSocket = inputSocket;
        this.inputPacket = new DatagramPacket(new byte[256], 256);
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void run() {
        while (!inputSocket.isClosed()) {
            try {
                inputSocket.receive(this.inputPacket);
                inputConsumer.addDatagramPacket(this.inputPacket);
            } catch (IOException e) {
                inputSocket.close();
            }
        }
    }
}
