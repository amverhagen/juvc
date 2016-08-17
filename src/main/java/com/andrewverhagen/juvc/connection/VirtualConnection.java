package com.andrewverhagen.juvc.connection;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class VirtualConnection extends Observable {

    private long timeOfLastValidInput;
    private final long timeOutTimeInNanoSeconds;

    private ConnectionState connectionState;
    private InetSocketAddress connectionAddress;

    private final InputHandler inputHandler;
    private final OutputSender outputSender;

    public VirtualConnection(InetSocketAddress connectionAddress, int timeOutTimeInMilliSeconds, InputHandler inputHandler, OutputSender outputSender) {
        this(connectionAddress, TimeUnit.MILLISECONDS.toNanos(timeOutTimeInMilliSeconds), inputHandler, outputSender);
    }

    public VirtualConnection(InetSocketAddress connectionAddress, long timeOutTimeInNanoSeconds, InputHandler inputHandler, OutputSender outputSender) {
        this.connectionAddress = connectionAddress;
        this.timeOutTimeInNanoSeconds = timeOutTimeInNanoSeconds;
        this.inputHandler = inputHandler;
        this.outputSender = outputSender;
        this.connectionState = ConnectionState.UNOPENED;
    }

    public synchronized boolean handleInput(DatagramPacket inputPacket) throws BufferUnderflowException {
        if (!this.isActive() && this.containsAddress(inputPacket.getSocketAddress())) {
            if (inputHandler.handleInput(inputPacket.getData()))
                this.receivedValidInput();
            return true;
        }
        return false;
    }

    public synchronized void sendOutput() {
        if (!this.isEnded()) {
            outputSender.sendOutput();
            if (this.connectionState == ConnectionState.UNOPENED)
                this.openConnection();
        }
    }

    public synchronized boolean containsAddress(SocketAddress addressToCheck) {
        return this.containsAddress((InetSocketAddress) addressToCheck);
    }

    public synchronized boolean containsAddress(VirtualConnection connectionToCheck) {
        return this.containsAddress(connectionToCheck.connectionAddress);
    }

    public synchronized boolean containsAddress(InetSocketAddress addressToCheck) {
        return AddressUtils.checkIfAddressesMatch(this.connectionAddress, addressToCheck);
    }

    public synchronized boolean isEnded() {
        this.refreshConnectionExpirationTime();
        return this.connectionState == ConnectionState.ENDED;
    }

    private synchronized boolean isActive() {
        return this.connectionState != ConnectionState.UNOPENED && !this.isEnded();
    }

    private synchronized void openConnection() {
        this.setConnectionState(ConnectionState.CONNECTING);
        this.setLastValidInputTimeToNow();
    }

    private synchronized void receivedValidInput() {
        if (this.connectionState == ConnectionState.CONNECTING)
            this.setConnectionState(ConnectionState.CONNECTED);
        this.setLastValidInputTimeToNow();
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

    private synchronized void refreshConnectionExpirationTime() {
        if (this.connectionState == ConnectionState.UNOPENED)
            return;
        if ((System.nanoTime() - timeOfLastValidInput) > timeOutTimeInNanoSeconds)
            this.setConnectionState(ConnectionState.ENDED);
    }
}
