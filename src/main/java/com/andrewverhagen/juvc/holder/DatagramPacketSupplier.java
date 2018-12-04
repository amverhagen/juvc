package com.andrewverhagen.juvc.holder;

import java.net.DatagramPacket;
import java.util.List;
import java.util.function.Supplier;

public interface PacketSupplier extends Supplier<List<DatagramPacket>> {
}
