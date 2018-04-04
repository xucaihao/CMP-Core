package com.cmp.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PingUtil {

    private static final Logger logger = LoggerFactory.getLogger(PingUtil.class);

    public static boolean ping(String ip, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port), 100);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("[PingUtil::ping] try close socket error...");
            }
        }
        return true;
    }
}
