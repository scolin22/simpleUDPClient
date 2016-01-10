package com._31645112.cpen431.A1;

public class SecretCodeClient {
    ApplicationLayer al;

    public SecretCodeClient(String remoteIP, int remotePort) {
        this.al = new ApplicationLayer(remoteIP, remotePort);
    }

    public String getSecretCode(int studentNumber) {
        return al.getSecretMessage(studentNumber);
    }
}
