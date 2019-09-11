package com.andrewverhagen.juvc;

import java.net.DatagramPacket;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReceivedPacketInputConsumer implements Consumer<DatagramPacket> {

    private AtomicBoolean receivedData = new AtomicBoolean(false);

    public boolean receivedData() {
        return this.receivedData();
    }

    @Override
    public void accept(DatagramPacket t) {
        this.receivedData.set(true);
    }
}