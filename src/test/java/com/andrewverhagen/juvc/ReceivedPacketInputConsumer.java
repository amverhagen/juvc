package com.andrewverhagen.juvc;

import com.andrewverhagen.juvc.connection.InputConsumer;

import java.net.DatagramPacket;

public class ReceivedPacketInputConsumer implements InputConsumer {

    private volatile boolean receivedData;

    public synchronized boolean receivedData() {
        return this.receivedData;
    }

    @Override
    public synchronized void addDatagramPacket(DatagramPacket inputData) {
        this.receivedData = true;
    }
}