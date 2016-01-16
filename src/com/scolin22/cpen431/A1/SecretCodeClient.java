package com.scolin22.cpen431.A1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class SecretCodeClient {
    ApplicationLayer al;
    ConcurrentHashMap<String, BlockingQueue<byte[]>> messages;


    public SecretCodeClient(String remoteIP, int remotePort, int timeout) {
        al = new ApplicationLayer(remoteIP, remotePort, timeout);
        messages = new ConcurrentHashMap<String, BlockingQueue<byte[]>>();
        // Producer thread
        Producer p = new Producer(al, messages);
        p.start();
    }

    public void getSecretCode(int studentNumber) {
        // Consumer thread
        Consumer c = new Consumer(al, messages, studentNumber);
        c.start();
    }
}
