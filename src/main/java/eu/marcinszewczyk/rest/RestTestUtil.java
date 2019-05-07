package eu.marcinszewczyk.rest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

class RestTestUtil {
    static ResponseWrapper get(String endpoint) throws IOException, URISyntaxException {
        HttpURLConnection http = (HttpURLConnection) new URI(endpoint).toURL().openConnection();
        http.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String body = br.lines().collect(Collectors.joining());
        return new ResponseWrapper(http.getResponseCode(), body);
    }

    static ResponseWrapper post(String endpoint, String body) throws IOException, URISyntaxException {
        HttpURLConnection http = (HttpURLConnection) new URI(endpoint).toURL().openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        OutputStream os = http.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(body);
        writer.flush();
        writer.close();
        os.close();

        http.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
        return new ResponseWrapper(
                http.getResponseCode(),
                br.lines().collect(Collectors.joining()));
    }

    static class ResponseWrapper {
        private int status;
        private String body;

        private ResponseWrapper(int status, String body) {
            this.status = status;
            this.body = body;
        }

        int getStatus() {
            return status;
        }

        String getBody() {
            return body;
        }
    }
}
