package com.andrewverhagen.juvc.connection;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class VirtualConnection extends Observable {

    private long timeOfLastValidInput;
    private final long timeOutTimeInNanoSeconds;

    private ConnectionState connectionState;
    private InetSocketAddress connectionAddress;

    private final InputConsumer inputConsumer;
    private final OutputProvider outputProvider;

    public VirtualConnection(InetSocketAddress connectionAddress, int timeOutTimeInMilliSeconds, InputConsumer inputConsumer, OutputProvider outputProvider) {
        this.connectionAddress = connectionAddress;
        this.timeOutTimeInNanoSeconds = TimeUnit.MILLISECONDS.toNanos(timeOutTimeInMilliSeconds);
        this.inputConsumer = inputConsumer;
        this.outputProvider = outputProvider;
        this.connectionState = ConnectionState.UNOPENED;
    }

    public void handleInput(DatagramPacket inputPacket) {
        if (this.isActive() && this.containsAddress(inputPacket.getSocketAddress())) {
            this.receivedValidInput();
            synchronized (inputConsumer) {
                inputConsumer.addInputData(inputPacket.getData());
            }
        }
    }

    public synchronized DatagramPacket getOutputPacket() {
        if (!this.isActive())
            return null;
        else {
            byte[] outputData;
            synchronized (outputProvider) {
                outputData = outputProvider.getOutputData();
            }
            return new DatagramPacket(outputData, outputData.length, this.connectionAddress);
        }
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
    }

    public synchronized void closeConnection() {
        this.setConnectionState(ConnectionState.ENDED);
    }

    private synchronized boolean isActive() {
        this.refreshConnectionState();
        return this.connectionState != ConnectionState.UNOPENED && this.connectionState != ConnectionState.ENDED;
    }

    private synchronized void receivedValidInput() {
        if (this.connectionState == ConnectionState.CONNECTING)
            this.setConnectionState(ConnectionState.CONNECTED);
        this.setLastValidInputTimeToNow();
    }

    private synchronized void refreshConnectionState() {
        if (this.connectionState == ConnectionState.UNOPENED || this.connectionState == ConnectionState.ENDED)
            return;
        if ((System.nanoTime() - timeOfLastValidInput) > timeOutTimeInNanoSeconds)
            this.setConnectionState(ConnectionState.ENDED);
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
