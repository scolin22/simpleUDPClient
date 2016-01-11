package com.scolin22.cpen431.test

import com.scolin22.cpen431.A1.ApplicationLayerEncoder

import java.nio.ByteBuffer

class ApplicationLayerEncoderTest extends GroovyTestCase {
    void testPackHeader() {
        def LOCAL_IP = InetAddress.getByName("12.34.56.78")
        def PORT = 4567
        def timestamp = 698455047
        def encoder = new ApplicationLayerEncoder(LOCAL_IP, PORT)

        byte[] b = [0xC, 0x22, 0x38, 0x4E, 0xD7, 0x11, 0x0, 0x0, 0x7, 0x94, 0xA1, 0x29, 0x0, 0x0, 0x0, 0x0]
        assert encoder.packHeader(timestamp, false) == b
    }

    void testPackPayload() {
        def LOCAL_IP = InetAddress.getByName("12.34.56.78")
        def PORT = 4567
        def encoder = new ApplicationLayerEncoder(LOCAL_IP, PORT)

        byte[] b = [0x0, 0x15, 0x15, 0x0]
        assert encoder.packPayload(1381632) == b

        b = [0x1, 0xE2, 0xDD, 0xB8]
        assert encoder.packPayload(31645112) == b
    }

    void testUnpackResponse() {
        def LOCAL_IP = InetAddress.getByName("12.34.56.78")
        def PORT = 4567
        def encoder = new ApplicationLayerEncoder(LOCAL_IP, PORT)
        def inBuf = ByteBuffer.allocate(36)

        byte[] b = [0xC, 0x22, 0x38, 0x4E, 0xD7, 0x11, 0x3, 0xC0, 0x17, 0xB5, 0xB3, 0x29, 0x0, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0xD5, 0x2, 0xF4, 0x66, 0x1C, 0x49, 0x84, 0x9B, 0x2B, 0x2F, 0xA9, 0x5A, 0x62, 0x32, 0x94, 0xBB]
        inBuf.put(b)

        assert encoder.unpackPayload(inBuf) == "D502F4661C49849B2B2FA95A623294BB"
    }
}
