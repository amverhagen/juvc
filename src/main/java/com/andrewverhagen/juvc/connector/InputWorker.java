package com.andrewverhagen.juvc.connector;

import com.andrewverhagen.juvc.holder.ConnectionHolder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class InputWorker extends Thread {

    private final DatagramSocket inputSocket;
    private final DatagramPacket inputPacket;
    private final ConnectionHolder connectionHolder;

    InputWorker(ConnectionHolder connectionHolder, DatagramSocket inputSocket) {
        this.inputSocket = inputSocket;
        this.inputPacket = new DatagramPacket(new byte[256], 256);
        this.connectionHolder = connectionHolder;
    }

    @Override
    public void run() {
        while (!inputSocket.isClosed()) {
            try {
                inputSocket.receive(this.inputPacket);
                connectionHolder.distributePacketToConnections(this.inputPacket);
            } catch (IOException e) {
                inputSocket.close();
            }
        }
    }
}
