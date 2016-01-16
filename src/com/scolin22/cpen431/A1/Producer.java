package com.scolin22.cpen431.A1;

import com.scolin22.cpen431.utils.StringUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Producer extends Thread {
    final static int QUEUE_CAPACITY = 16;
    final static int MAX_PAYLOAD_SIZE = 256;
    private static Logger log = Logger.getLogger(Producer.class.getName());
    ConcurrentHashMap<String, BlockingQueue<byte[]>> messages;
    ApplicationLayer inAL;
    ByteBuffer inBuf;

    public Producer(ApplicationLayer inAL, ConcurrentHashMap<String, BlockingQueue<byte[]>> messages) {
        this.inAL = inAL;
        this.messages = messages;

        inBuf = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);
    }

    @Override
    public void run() {
        while (true) {
            if (inAL.getMessage(inBuf)) {
                String UID = StringUtils.byteArrayToHexString(inAL.encoder.unpackHeader(inBuf));
                BlockingQueue<byte[]> queue = messages.get(UID);
                try {
                    if (queue == null) {
                        queue = new LinkedBlockingQueue<byte[]>(QUEUE_CAPACITY);
                        queue.put(inAL.encoder.unpackPayload(inBuf));
                        messages.put(UID, queue);
                    } else {
                        byte[] b = inAL.encoder.unpackPayload(inBuf);
                        queue.put(inAL.encoder.unpackPayload(inBuf));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
