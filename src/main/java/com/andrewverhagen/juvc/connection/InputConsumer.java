package com.andrewverhagen.juvc.connection;

import java.net.DatagramPacket;

public interface InputConsumer {
    void addDatagramPacket(DatagramPacket inputData);
}
