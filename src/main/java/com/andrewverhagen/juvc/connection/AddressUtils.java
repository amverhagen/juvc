package com.andrewverhagen.juvc.connection;

import java.net.InetSocketAddress;

class AddressUtils {

    static boolean checkIfAddressesMatch(InetSocketAddress inetSocketAddressOne, InetSocketAddress inetSocketAddressTwo) {
        if (inetSocketAddressOne == null || inetSocketAddressTwo == null)
            return false;
        if (compareHostNames(inetSocketAddressOne.getHostName(), inetSocketAddressTwo.getHostName()))
            if (inetSocketAddressOne.getPort() == inetSocketAddressTwo.getPort())
                return true;
        return false;
    }

    static boolean compareHostNames(String hostNameOne, String hostNameTwo) {
        return !(hostNameOne == null || hostNameTwo == null) && hostNameOne.equals(hostNameTwo);
    }
}
