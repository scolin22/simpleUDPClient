package com.scolin22.cpen431.A1;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Invalid number of arguments: e.g. java -jar simpleUDPClient.jar <IP> <port> <ID>");
            return;
        }

        SecretCodeClient client = new SecretCodeClient(args[0], Integer.parseInt(args[1]));

        System.out.println(client.getSecretCode(Integer.parseInt(args[2])));
    }
}
