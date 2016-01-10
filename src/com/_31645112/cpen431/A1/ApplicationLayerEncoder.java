package com._31645112.cpen431.A1;

import com._31645112.cpen431.utils.StringUtils;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public byte[] getHeader(int timestamp, boolean randPad) {
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

    public byte[] getPayload(int studentNumber) {
        ByteBuffer appPayload = ByteBuffer.allocate(CLIENT_PAYLOAD_SIZE);

        appPayload.putInt(studentNumber);

        return appPayload.array();
    }

    public String decodeResponse(ByteBuffer inBuf) {
        int secretCodeLength = inBuf.order(ByteOrder.LITTLE_ENDIAN).getInt(UNIQUE_ID_LENGTH);
        log.info("Secret code length: " + secretCodeLength);

        byte[] secretCode = new byte[secretCodeLength];
        System.arraycopy(inBuf.array(), SECRET_CODE_OFFSET, secretCode, 0, secretCodeLength);

        return StringUtils.byteArrayToHexString(secretCode);
    }

    // http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
