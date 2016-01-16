package com.scolin22.cpen431.A1;

import com.scolin22.cpen431.utils.StringUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Consumer extends Thread {
    final static int QUEUE_CAPACITY = 16;
    final static int MAX_PAYLOAD_SIZE = 256;
    private static Logger log = Logger.getLogger(Consumer.class.getName());
    ConcurrentHashMap<String, BlockingQueue<byte[]>> messages;
    ApplicationLayer outAL;
    int studentNumber;
    ByteBuffer outBuf;

    public Consumer(ApplicationLayer outAL, ConcurrentHashMap<String, BlockingQueue<byte[]>> messages, int studentNumber) {
        this.outAL = outAL;
        this.messages = messages;
        this.studentNumber = studentNumber;

        outBuf = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);
    }

    @Override
    public void run() {
        outAL.setCodeMessage(outBuf, studentNumber);
        String UID = StringUtils.byteArrayToHexString(outAL.encoder.unpackHeader(outBuf));
        BlockingQueue<byte[]> sharedQueue = new LinkedBlockingQueue<byte[]>(QUEUE_CAPACITY);

        messages.put(UID, sharedQueue);
        //TODO: set the correct timeout and retry policy
        while (true) {
            outAL.sendCodeMessage(outBuf);
            log.info("Trying to send message.");
            try {
                byte[] payload = sharedQueue.poll(1000, TimeUnit.MILLISECONDS);
                if (payload != null) {
                    String response = StringUtils.byteArrayToHexString(payload);
                    log.info("Secret: " + response);
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
