package com.scolin22.cpen431.A1;

import com.scolin22.cpen431.utils.StringUtils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Logger;

public class ApplicationLayer {
    private static Logger log = Logger.getLogger(ApplicationLayer.class.getName());

    final static String LOCAL_IP = "12.34.56.78";
    final static int MAX_PAYLOAD_SIZE = 256;
    InetAddress remoteIP;
    DatagramSocket socket;
    int remotePort;
    ApplicationLayerEncoder encoder;
    ByteBuffer outBuf;
    ByteBuffer inBuf;

    public ApplicationLayer(String remoteIP, int remotePort) {
        try {
            this.remoteIP = InetAddress.getByName(remoteIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.remotePort = remotePort;
        outBuf = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);
        inBuf = ByteBuffer.allocate(MAX_PAYLOAD_SIZE);
        //TODO: Consider using java.nio.DatagramChannel, non-block IO
        try {
            socket = new DatagramSocket(4567);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            encoder = new ApplicationLayerEncoder(InetAddress.getByName(LOCAL_IP), socket.getLocalPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public String getSecretMessage(int studentNumber) {
        clearBuffers();
        setMessage(studentNumber);

        getMessage();

        String response = StringUtils.byteArrayToHexString(encoder.unpackPayload(inBuf));
        log.info("Secret: " + response);

        clearBuffers();
        return response;
    }

    private boolean getMessage() {
        int attempts = 10;
        while (attempts > 0) {
            attemptSendMessage();
            byte[] b1 = encoder.unpackHeader(inBuf);
            byte[] b2 = encoder.unpackHeader(outBuf);
            if (Arrays.equals(b1, b2)) {
                return true;
            }
        }
        return false;
    }

    private void clearBuffers() {
        inBuf.clear();
        outBuf.clear();
    }

    private void setMessage(int studentNumber) {
        outBuf.put(encoder.packHeader((int) System.currentTimeMillis(), true));

        log.info("Sending ID: " + studentNumber);
        outBuf.put(encoder.packPayload(studentNumber));
    }

    private void attemptSendMessage() {
        int attempts = 3;
        int timeout = 100;
        while (attempts > 0) {
            try {
                socket.setSoTimeout(timeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            DatagramPacket outPacket = new DatagramPacket(outBuf.array(), MAX_PAYLOAD_SIZE, remoteIP, remotePort);
            DatagramPacket inPacket  = new DatagramPacket(inBuf.array(), MAX_PAYLOAD_SIZE);
            try {
                socket.send(outPacket);
                socket.receive(inPacket);
                break;
            } catch (InterruptedIOException e) {
                log.info("Wait on remote host timed out.");
                timeout *= 2;
                attempts--;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (attempts == 0)
            log.info("Retry attempts failed.");
    }
}
