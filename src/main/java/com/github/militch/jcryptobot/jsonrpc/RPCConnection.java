package com.github.militch.jcryptobot.jsonrpc;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.*;

public class RPCConnection {
    private static final String[] methods = new String[]{
            "GET", "POST", "OPTION", "HEAD", "PUT", "DELETE", "TRACE", "CONNECT"};
    private static final Logger logger = LogManager.getLogger();
    private final Socket rw;

    public RPCConnection(Socket rw) {
        this.rw = rw;
    }

    private static boolean validMethod(String method){
        return method != null &&
                method.length() > 0 &&
                Arrays.asList(methods).contains(method);
    }

    private static void echoError(OutputStream os, int code, String msg) throws IOException {
        String errorHeaders = "\r\nContent-Type: text/plain; charset=utf-8\r\nConnection: close\r\n\r\n";
        String codeAndMsg = String.format("%d %s", code, msg);
        String error = String.format("HTTP/1.1 %s", codeAndMsg);
        IOUtils.write(error + errorHeaders + msg, os, Charset.defaultCharset());
    }

    private static boolean validHttpVersion(String proto){
        return proto != null && proto.equals("HTTP/1.1");
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while((line = reader.readLine()) != null) {
            if(line.length() == 0) {
                break;
            }
            String[] kvs = line.split(":");
            String key = kvs[0];
            key = key.trim();
            String value = kvs[1];
            headers.put(key, value);
        }
        return headers;
    }

    public void serve(){
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        try {
            //rw.getRemoteSocketAddress();
            is = rw.getInputStream();
            os = rw.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String firstLine = br.readLine();
            if(Strings.isNullOrEmpty(firstLine)){
                return;
            }
            String[] s = firstLine.split(" ");
            if (s.length != 3) {
                logger.warn("Bad HTTP request: {}", firstLine);
                return;
            }
            String method = s[0]; String uri = s[1]; String version = s[2];
            if (!validMethod(method)) {
                logger.warn("Bad HTTP request method: {}", method);
                return;
            }
            if (!validHttpVersion(version)) {
                logger.warn("Bad HTTP version: {}", version);
                return;
            }
            HttpRequest request = new HttpRequest(method, uri, version);
            // read headers
            Map<String, String> headers = readHeaders(br);
            request.setHeaders(headers);
            logger.info("line: {}", firstLine);
            echoError(os, 404, "Bad Request");
            IOUtils.closeQuietly(rw);
        }catch (IOException e){
            logger.error(e);
        }finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }
}
