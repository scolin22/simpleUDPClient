package com.scolin22.cpen431.A1;

public class Main {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Invalid number of arguments: e.g. java -jar simpleUDPClient.jar <IP> <port> <timeout(ms)> <ID>");
            return;
        }

        SecretCodeClient client = new SecretCodeClient(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));

        client.getSecretCode(Integer.parseInt(args[3]));
    }
}
