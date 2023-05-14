package com.github.militch.jcryptobot.jsonrpc;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JsonRPCServer {
    private static final Logger logger = LogManager.getLogger();
    private final ExecutorService es = Executors.newCachedThreadPool();
    public final Map<String, Integer> methods = new HashMap<>();
    private boolean closed;
    private boolean running;
    private ServerSocket serverSocket;
    public void registerMethod(String method) {
        methods.put(method, 1);
    }
    public void run(String host, int port) throws IOException {
        if (running) {
            return;
        }
        running = true;
        InetSocketAddress isa = InetSocketAddress.createUnresolved(host, port);
        serverSocket = new ServerSocket(isa.getPort(), 50, isa.getAddress());
        logger.info("Listen on: {}:{}", isa.getHostName(), isa.getPort());
        while (!closed) {
            Socket rw = serverSocket.accept();
            RPCConnection conn = new RPCConnection(rw);
            es.execute(conn::serve);
        }
    }
    public void close(){
        this.running = false;
        IOUtils.closeQuietly(serverSocket);
        this.closed = true;
    }
}

