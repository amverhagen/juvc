package com.andrewverhagen.juvc.holder;

import java.net.DatagramPacket;
import java.util.List;

public interface PacketProvider {

    List<DatagramPacket> getPackets();
}
