package com.andrewverhagen.juvc.connection;

import java.net.DatagramPacket;
import java.util.function.Consumer;

public interface InputConsumer extends Consumer<DatagramPacket> {
}
