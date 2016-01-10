package com._31645112.cpen431.A1;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.nio.ByteBuffer;

public class ApplicationLayer {
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
        attemptSendMessage();
        String response = encoder.decodeResponse(inBuf);
        clearBuffers();
        return response;
    }

    private void clearBuffers() {
        inBuf.clear();
        outBuf.clear();
    }

    private void setMessage(int studentNumber) {
        outBuf.put(encoder.getHeader((int) System.currentTimeMillis(), true));
        outBuf.put(encoder.getPayload(studentNumber));
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

            DatagramPacket packet = new DatagramPacket(outBuf.array(), MAX_PAYLOAD_SIZE, remoteIP, remotePort);

            try {
                socket.send(packet);

                packet = new DatagramPacket(outBuf.array(), MAX_PAYLOAD_SIZE);
                socket.receive(packet);
                inBuf.put(packet.getData());
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
}
