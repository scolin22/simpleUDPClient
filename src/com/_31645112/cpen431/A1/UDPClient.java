package com._31645112.cpen431.A1;

import java.net.InetAddress;

/**
 * Created by colin on 2016-01-08.
 */
public class UDPClient {
    ApplicationLayer al;

    public UDPClient(String remoteIP, int remotePort) {
        this.al = new ApplicationLayer(remoteIP, remotePort);
    }

    public String getSecretCode(int studentNumber) {
        return al.getSecretMessage(studentNumber);
    }
}
