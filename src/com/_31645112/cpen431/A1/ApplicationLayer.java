package com._31645112.cpen431.A1;

import com._31645112.cpen431.utils.ByteOrder;
import com._31645112.cpen431.utils.StringUtils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.Random;

import static java.lang.Math.pow;

public class ApplicationLayer {
    InetAddress remoteIP;
    int remotePort;
    DatagramSocket socket;

    final static int UNIQUE_ID_LENGTH = 16;
    final static int IP_LENGTH = 4;
    final static int IP_OFFSET = 0;
    final static int PORT_LENGTH = 2;
    final static int PORT_OFFSET = IP_OFFSET + IP_LENGTH;
    final static int PAD_LENGTH = 2;
    final static int PAD_OFFSET = PORT_OFFSET + PORT_LENGTH;
    final static int TIMESTAMP_LENGTH = 8;
    final static int TIMESTAMP_OFFSET = PAD_OFFSET + PAD_LENGTH;

    final static String LOCAL_IP = "69.69.69.69";

    final static int MAX_PAYLOAD_SIZE = 256;
    final static int CLIENT_PAYLOAD_SIZE = 4;
    final static int SECRET_CODE_COUNT_LENGTH = 4;
    final static int SECRET_CODE_OFFSET = UNIQUE_ID_LENGTH + SECRET_CODE_COUNT_LENGTH;
    byte[] outBuf;
    byte[] inBuf;

    public ApplicationLayer(String remoteIP, int remotePort) {
        try {
            this.remoteIP = InetAddress.getByName(remoteIP);
            this.remotePort = remotePort;
            outBuf = new byte[MAX_PAYLOAD_SIZE];
            inBuf = new byte[MAX_PAYLOAD_SIZE];
            socket = new DatagramSocket();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void setMessage(int studentNumber) {
        byte[] uniqueID = buildUniqueID();
        System.arraycopy(uniqueID, 0, outBuf, 0, uniqueID.length);
        byte[] applicationPayload = buildApplicationPayload(studentNumber);
        System.arraycopy(applicationPayload, 0, outBuf, uniqueID.length, applicationPayload.length);
    }

    public String getSecretMessage(int studentNumber) {
        setMessage(studentNumber);

        attemptResponsePayload();

        return extractMessage();
    }

    private void attemptResponsePayload() {
        int attempts = 3;
        int timeout = 100;
        while (attempts > 0) {
            try {
                socket.setSoTimeout(timeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            DatagramPacket packet = new DatagramPacket(outBuf, outBuf.length, remoteIP, remotePort);

            try {
                socket.send(packet);

                packet = new DatagramPacket(outBuf, outBuf.length);
                socket.receive(packet);
                inBuf = packet.getData();
                break;
            } catch (InterruptedIOException e) {
                System.err.println("Wait on remote host timed out.");
                timeout *= 2;
                attempts--;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String extractMessage() {
        int secretCodeLength = ByteOrder.leb2int(inBuf, UNIQUE_ID_LENGTH);

        byte[] secretCode = new byte[secretCodeLength];
        System.arraycopy(inBuf, SECRET_CODE_OFFSET, secretCode, 0, secretCodeLength);

        return StringUtils.byteArrayToHexString(secretCode);
    }

    private byte[] buildUniqueID() {
        int TEMP_SIZE = 8;
        byte[] uniqueID = new byte[UNIQUE_ID_LENGTH];
        byte[] temp;

        try {
            InetAddress localIP = InetAddress.getByName(LOCAL_IP);
            System.arraycopy(ByteOrder.reverse(localIP.getAddress()), 0, uniqueID, 0, IP_LENGTH);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        temp = new byte[TEMP_SIZE];
        ByteOrder.int2leb(socket.getLocalPort(), temp, 0);
        System.arraycopy(temp, 0, uniqueID, PORT_OFFSET, PORT_LENGTH);

        temp = new byte[TEMP_SIZE];
        ByteOrder.int2leb(randInt(0, (int) pow(2, 16) - 1), temp, 0);
        System.arraycopy(temp, 0, uniqueID, PAD_OFFSET, PAD_LENGTH);

        temp = new byte[TEMP_SIZE];
        ByteOrder.int2leb((int) System.currentTimeMillis(), temp, 0);
        System.arraycopy(temp, 0, uniqueID, TIMESTAMP_OFFSET, TIMESTAMP_LENGTH);

        return uniqueID;
    }

    private static byte[] buildApplicationPayload(int studentNumber) {
        byte[] appPayload = new byte[CLIENT_PAYLOAD_SIZE];

        ByteOrder.int2leb(studentNumber, appPayload, 0);
        appPayload = ByteOrder.reverse(appPayload);

        return appPayload;
    }

    // http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
