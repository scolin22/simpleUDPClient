package com.scolin22.cpen431.A1;

public class SecretCodeClient {
    ApplicationLayer al;

    public SecretCodeClient(String remoteIP, int remotePort, int timeout) {
        this.al = new ApplicationLayer(remoteIP, remotePort, timeout);
    }

    public String getSecretCode(int studentNumber) {
        return al.getSecretMessage(studentNumber);
    }
}
