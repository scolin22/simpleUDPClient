package com.scolin22.cpen431.A1;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class ApplicationLayer {
    final static String LOCAL_IP = "12.34.56.78";
    final static int MAX_PAYLOAD_SIZE = 256;
    private static Logger log = Logger.getLogger(ApplicationLayer.class.getName());
    InetAddress remoteIP;
    DatagramSocket socket;
    int remotePort;
    int timeout;
    ApplicationLayerEncoder encoder;

    public ApplicationLayer(String remoteIP, int remotePort, int timeout) {
        try {
            this.remoteIP = InetAddress.getByName(remoteIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.timeout = timeout;
        this.remotePort = remotePort;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            encoder = new ApplicationLayerEncoder(InetAddress.getByName(LOCAL_IP), socket.getLocalPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void setCodeMessage(ByteBuffer outBuf, int studentNumber) {
        outBuf.put(encoder.packHeader((int) System.currentTimeMillis(), true));
        log.info("Sending ID: " + studentNumber);
        outBuf.put(encoder.packPayload(studentNumber));
    }

    public void sendCodeMessage(ByteBuffer outBuf) {
        DatagramPacket outPacket = new DatagramPacket(outBuf.array(), MAX_PAYLOAD_SIZE, remoteIP, remotePort);
        try {
            socket.send(outPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getMessage(ByteBuffer inBuf) {
        try {
            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket inPacket  = new DatagramPacket(inBuf.array(), MAX_PAYLOAD_SIZE);
        try {
            socket.receive(inPacket);
        } catch (InterruptedIOException e) {
//            log.info("Wait on remote host timed out.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
