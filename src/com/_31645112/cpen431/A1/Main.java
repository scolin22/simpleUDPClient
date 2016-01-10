package com._31645112.cpen431.A1;

public class Main {

    public static void main(String[] args) {
        SecretCodeClient client = new SecretCodeClient(args[0], Integer.parseInt(args[1]));

        System.out.println(client.getSecretCode(Integer.parseInt(args[2])));
    }
}
