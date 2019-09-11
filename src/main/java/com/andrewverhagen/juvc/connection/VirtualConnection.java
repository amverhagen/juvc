package com.andrewverhagen.juvc.connection;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class VirtualConnection extends Observable {

    private long timeOfLastValidInput;
    private final int timeOutTimeInMilliSeconds;
    private final long timeOutTimeInNanoSeconds;

    private ConnectionState connectionState;
    private InetSocketAddress connectionAddress;

    private final Consumer<DatagramPacket> packetConsumer;
    private final OutputSupplier outputSupplier;

    public VirtualConnection(InetSocketAddress connectionAddress, int timeOutTimeInMilliSeconds,
            Consumer<DatagramPacket> packetConsumer, OutputSupplier outputProvider) {
        this.connectionAddress = connectionAddress;
        this.timeOutTimeInMilliSeconds = timeOutTimeInMilliSeconds;
        this.timeOutTimeInNanoSeconds = TimeUnit.MILLISECONDS.toNanos(timeOutTimeInMilliSeconds);
        this.packetConsumer = packetConsumer;
        this.outputSupplier = outputProvider;
        this.connectionState = ConnectionState.UNOPENED;
    }

    public VirtualConnection(VirtualConnection connection) {
        this(connection.connectionAddress, connection.timeOutTimeInMilliSeconds, connection.packetConsumer,
                connection.outputSupplier);
    }

    public void handleInput(DatagramPacket inputPacket) {
        if (this.isActive() && this.equals(inputPacket.getSocketAddress())) {
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

    @Override
    public boolean equals(Object obj) {
        return this.connectionAddress.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.connectionAddress.hashCode();
    }
}
