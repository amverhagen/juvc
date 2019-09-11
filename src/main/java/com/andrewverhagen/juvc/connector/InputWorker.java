package com.andrewverhagen.juvc.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;

class InputWorker extends Thread {

    private final DatagramSocket inputSocket;
    private final DatagramPacket inputPacket;
    private final Consumer<DatagramPacket> inputConsumer;

    InputWorker(Consumer<DatagramPacket> inputConsumer, DatagramSocket inputSocket) {
        this.inputSocket = inputSocket;
        this.inputPacket = new DatagramPacket(new byte[256], 256);
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void run() {
        while (!inputSocket.isClosed()) {
            try {
                inputSocket.receive(this.inputPacket);
                inputConsumer.accept(this.inputPacket);
            } catch (IOException e) {
                inputSocket.close();
            }
        }
    }
}
