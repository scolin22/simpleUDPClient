package com._31645112.cpen431.A1;

public class Main {

    public static void main(String[] args) {
        UDPClient client = new UDPClient(args[0], Integer.parseInt(args[1]));

        client.getSecretCode(Integer.parseInt(args[2]));
    }
}
