package org.tcelor.quarkus.api.service.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLSocketFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SocketHttp {

    private static SocketHttp socketHttp = null;

    private SocketHttp() {
    }
    
    public static SocketHttp getSocketHttp() {
        if (socketHttp == null) {
            socketHttp = new SocketHttp();
        }
        return socketHttp;
    }

    public CompletableFuture<String> execute(String host, String path) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder response = new StringBuilder();
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            try (Socket socket = factory.createSocket("catfact.ninja", 443)) {
                StringBuilder httpRequest = new StringBuilder();
                httpRequest.append("GET ");
                httpRequest.append(path);
                httpRequest.append(" HTTP/1.1\r\n");
                httpRequest.append("Host: ");
                httpRequest.append(host);
                httpRequest.append("\r\n");
                httpRequest.append("User-Agent: JavaCatFactClient/1.0\r\n");
                httpRequest.append("\r\n");
                OutputStream output = socket.getOutputStream();
                byte[] requestBytes = httpRequest.toString().getBytes(StandardCharsets.UTF_8);
                output.write(requestBytes, 0, requestBytes.length);
                output.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) == null) {
                    System.out.println("Waiting ..");
                    Thread.sleep(10);
                } //Wait to get response
                while ((line = reader.readLine()) == null || !line.isEmpty()) { //Find separator of Header and body in HTTP response
                }
                reader.readLine(); //Remove the line after separator
                if ((line = reader.readLine()) == null || line.isEmpty() || line.isBlank()) {
                    response.append("Cannot found response body");
                } else {
                    response.append(new ObjectMapper().readTree(line).get("fact").asText()); //Save the result
                }
                output.close();
                reader.close();
                socket.close();
            } catch (Exception e) { //For easier handle
                System.out.println(e);
                response.append("Error by handling response.");
            }
            return response.toString();
        });
    }
}
