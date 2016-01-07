package com._31645112.eece411.A1;

import java.io.IOException;

/**
 * Created by colin on 2016-01-06.
 */
public class QuoteServer {
    public static void main(String[] args) throws IOException {
        new QuoteServerThread().start();
    }
}
