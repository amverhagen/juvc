package com.andrewverhagen.juvc.connection;

import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddressUtilsTest {

    @Test
    public void compareHostNames_NullValuesGiven_ShouldReturnFalse() {
        String hostNameOne = null;
        String hostNameTwo = null;
        assertFalse(AddressUtils.compareHostNames(hostNameOne, hostNameTwo));
    }

    @Test
    public void compareHostNames_EqualValuesGiven_ShouldReturnTrue() {
        String hostNameOne = "1.1.1.1";
        String hostNameTwo = "1.1.1.1";
        assertTrue(AddressUtils.compareHostNames(hostNameOne, hostNameTwo));
    }

    @Test
    public void checkIfAddressesMatch_DifferentPortsGiven_ShouldReturnFalse() {
        InetSocketAddress addressOne = new InetSocketAddress(9001);
        InetSocketAddress addressTwo = new InetSocketAddress(9002);
        assertFalse(AddressUtils.checkIfAddressesMatch(addressOne, addressTwo));
    }

    @Test
    public void checkIfAddressesMatch_EqualAddressesGiven_ShouldReturnTrue() {
        InetSocketAddress addressOne = new InetSocketAddress(9002);
        InetSocketAddress addressTwo = new InetSocketAddress(9002);
        assertTrue(AddressUtils.checkIfAddressesMatch(addressOne, addressTwo));
    }

    @Test
    public void checkIfAddressesMatch_NullValueGiven_ShouldReturnFalse() {
        InetSocketAddress addressOne = null;
        InetSocketAddress addressTwo = new InetSocketAddress(9002);
        assertFalse(AddressUtils.checkIfAddressesMatch(addressOne, addressTwo));
    }
}
