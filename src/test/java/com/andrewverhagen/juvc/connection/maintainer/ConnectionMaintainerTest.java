package com.andrewverhagen.juvc.connection.maintainer;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConnectionMaintainerTest {

    @Test
    public void testInvalidConstructorArgument() {
        try {
            new ConnectionMaintainer(0);
            fail("Initialization did not throw exception.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void holdingConnection() throws Exception {

    }

    @Test
    public void addConnections() throws Exception {

    }

    @Test
    public void atConnectionCapacity() throws Exception {

    }

}