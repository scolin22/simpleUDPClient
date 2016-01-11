package com.scolin22.cpen431.A1;

import com.scolin22.cpen431.utils.StringUtils;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import static java.lang.Math.pow;

/*
    Application Layer Protocol
    Request
        Unique ID (Little Endian)
            Client IP:     4
            Port:          2
            Rand:          2
            Timestamp:     8
        Payload (Big Endian)
            StudentNumber: 4
    Response
        Unique ID (Little Endian)
        Payload (Little Endian)
            Length:        4
            Code (byte[]): X
 */
public class ApplicationLayerEncoder {
    private static Logger log = Logger.getLogger(ApplicationLayerEncoder.class.getName());

    final static int UNIQUE_ID_LENGTH = 16;
    final static int CLIENT_PAYLOAD_SIZE = 4;
    final static int SECRET_CODE_COUNT_LENGTH = 4;
    final static int SECRET_CODE_OFFSET = UNIQUE_ID_LENGTH + SECRET_CODE_COUNT_LENGTH;
    InetAddress clientIP;
    int port;

    public ApplicationLayerEncoder(InetAddress clientIP, int port) {
        this.clientIP = clientIP;
        this.port = port;
    }

    public byte[] packHeader(int timestamp, boolean randPad) {
        ByteBuffer uniqueID = ByteBuffer.allocate(UNIQUE_ID_LENGTH).order(ByteOrder.LITTLE_ENDIAN);

        uniqueID.put(clientIP.getAddress());
        uniqueID.putShort((short) port);
        if (randPad) {
            uniqueID.putShort((short) randInt(0, (int) pow(2, 16) - 1));
        } else {
            uniqueID.putShort((short) 0);
        }
        uniqueID.putLong(timestamp);

        return uniqueID.array();
    }

    public byte[] packPayload(int studentNumber) {
        ByteBuffer appPayload = ByteBuffer.allocate(CLIENT_PAYLOAD_SIZE);

        appPayload.putInt(studentNumber);

        return appPayload.array();
    }

    public byte[] unpackPayload(ByteBuffer inBuf) {
        int secretCodeLength = inBuf.order(ByteOrder.LITTLE_ENDIAN).getInt(UNIQUE_ID_LENGTH);
        log.info("Secret code length: " + secretCodeLength);
        return Arrays.copyOfRange(inBuf.array(), SECRET_CODE_OFFSET, SECRET_CODE_OFFSET + secretCodeLength);
    }

    public byte[] unpackHeader(ByteBuffer bb) {
        return Arrays.copyOfRange(bb.array(), 0, UNIQUE_ID_LENGTH);
    }

    // http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
