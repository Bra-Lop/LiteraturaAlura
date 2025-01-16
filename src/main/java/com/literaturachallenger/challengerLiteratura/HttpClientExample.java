package com.literaturachallenger.challengerLiteratura;

import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Configuration
public class HttpClientExample { // Cambia el nombre de la clase a algo más descriptivo

    public String makeHttpRequest(String url) { // Cambia el nombre del método
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body(); // Devuelve el cuerpo de la respuesta
        } catch (IOException | InterruptedException e) { // Captura InterruptedException también
            e.printStackTrace(); // Imprime la traza de la excepción para depuración
            return null; // O lanza una excepción personalizada
        }
    }

    public static void main(String[] args) {
        HttpClientExample example = new HttpClientExample();
        String response = example.makeHttpRequest("https://gutendex.com/books");
        if (response != null) {
            System.out.println(response);
        }
    }
}