package com.andrewverhagen.juvc.connection;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class VirtualConnection extends Observable {

    private long timeOfLastValidInput;
    private final long timeOutTimeInNanoSeconds;

    private ConnectionState connectionState;
    private InetSocketAddress connectionAddress;

    private final DatagramPacketConsumer packetConsumer;
    private final OutputSupplier outputSupplier;

    public VirtualConnection(InetSocketAddress connectionAddress, int timeOutTimeInMilliSeconds,
            DatagramPacketConsumer packetConsumer, OutputSupplier outputProvider) {
        this.connectionAddress = connectionAddress;
        this.timeOutTimeInNanoSeconds = TimeUnit.MILLISECONDS.toNanos(timeOutTimeInMilliSeconds);
        this.packetConsumer = packetConsumer;
        this.outputSupplier = outputProvider;
        this.connectionState = ConnectionState.UNOPENED;
    }

    public void handleInput(DatagramPacket inputPacket) {
        if (this.isActive() && this.containsAddress(inputPacket.getSocketAddress())) {
            this.receivedInput();
            packetConsumer.accept(inputPacket);
        }
    }

    public synchronized DatagramPacket getOutputPacket() {
        if (this.isActive()) {
            byte[] outputData = outputSupplier.get();
            return new DatagramPacket(outputData, outputData.length, this.connectionAddress);
        }
        return null;
    }

    public boolean containsAddress(SocketAddress addressToCheck) {
        return this.containsAddress((InetSocketAddress) addressToCheck);
    }

    public boolean containsAddress(VirtualConnection connectionToCheck) {
        return this.containsAddress(connectionToCheck.connectionAddress);
    }

    public boolean containsAddress(InetSocketAddress addressToCheck) {
        return AddressUtils.checkIfAddressesMatch(this.connectionAddress, addressToCheck);
    }

    public synchronized void openConnection() {
        if (this.connectionState == ConnectionState.UNOPENED) {
            this.setConnectionState(ConnectionState.CONNECTING);
            this.setLastValidInputTimeToNow();
        }
        // throw ConnectionAlreadyOpenConnection here
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
        observer.update(this, this.connectionState);
    }

    public synchronized void closeConnection() {
        this.setConnectionState(ConnectionState.CLOSED);
    }

    private synchronized boolean isActive() {
        this.refreshConnectionState();
        return this.connectionState.isAnOpenState();
    }

    private synchronized void receivedInput() {
        if (this.connectionState == ConnectionState.CONNECTING)
            this.setConnectionState(ConnectionState.CONNECTED);
        this.setLastValidInputTimeToNow();
    }

    private synchronized void refreshConnectionState() {
        if (this.connectionState.isAnOpenState()) {
            if ((System.nanoTime() - timeOfLastValidInput) > timeOutTimeInNanoSeconds)
                this.setConnectionState(ConnectionState.CLOSED);
        }
        return;
    }

    private synchronized void setLastValidInputTimeToNow() {
        this.timeOfLastValidInput = System.nanoTime();
    }

    private synchronized void setConnectionState(ConnectionState newConnectionState) {
        if (this.connectionState.canMoveToConnectionLevel(newConnectionState)) {
            this.connectionState = newConnectionState;
            this.setChanged();
            this.notifyObservers(newConnectionState);
        }
    }
}
