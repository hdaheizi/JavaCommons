package top.daheizi.commons.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import top.daheizi.commons.util.MessageFormatter;

public class MyHttpServer {
    private final static int TCP_PORT = 80;
    private final static String FORMAT_1 = "The {0}st times!";

    public static void main(String[] args) throws IOException {
        startSocketServer();
//        startApacheServer();

    }
    
    public static void startApacheServer() throws IOException {
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(TCP_PORT)
                .registerHandler("/times", new HttpRequestHandler() {
                    int i = 0;
                    @Override
                    public void handle(HttpRequest request,
                            HttpResponse response, HttpContext context)
                            throws HttpException, IOException {
                        response.setEntity(new StringEntity(MessageFormatter.format(FORMAT_1, ++i)));
                    }
                    
                })
                .create();
        server.start();
    }
    
    
    public static void startSocketServer() throws IOException {
        ServerSocket ss = new ServerSocket(TCP_PORT);
        int i = 0;
        while (i < 10) {
            Socket socket = ss.accept();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String buffer = null;
            buffer = br.readLine();
            System.out.println(buffer);
            if (buffer != null) {
                StringTokenizer st = new StringTokenizer(buffer);
                st.nextToken();
                if (st.nextToken().startsWith("/times")) {
                    ++i;
                }
            }
            while ((buffer = br.readLine()) != null && !buffer.equals("")) {
                System.out.println(buffer);
            }
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            bw.write("HTTP/1.1 200 OK\r\n");
            bw.write("Content-Type: text/html; charset=UTF-8\r\n\r\n");
            bw.write(MessageFormatter.format(FORMAT_1, i));
            bw.flush();
            bw.close();
            br.close();
            socket.close();
        }
        ss.close();
    }
}
