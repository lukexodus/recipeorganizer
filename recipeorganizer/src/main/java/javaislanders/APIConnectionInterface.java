package javaislanders;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class APIConnectionInterface {
    private String baseUrl = "https://api.edamam.com/api/nutrition-details";
    private String app_id = "d7ee0a3e";
    private String app_key = "f17a12a0e315dacab18b8d5688083b2c";
    private HttpClient client = null;

    private String uriWithParams = baseUrl + "?app_id=" + app_id + "&app_key=" + app_key;

    public APIConnectionInterface() {
        if (client == null) {
            this.client = HttpClient.newHttpClient();
        }
    }

    public HttpClient getClient() {
        return client;
    }

    public String sendRequest(String payload) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl))
            .uri(URI.create(uriWithParams))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();
        
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }
}
