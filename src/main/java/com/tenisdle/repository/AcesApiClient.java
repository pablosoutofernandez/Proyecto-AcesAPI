package com.tenisdle.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenisdle.config.AppConfig;
import com.tenisdle.config.JacksonConfig;
import com.tenisdle.model.ApiResponse;
import com.tenisdle.model.Player;
import com.tenisdle.model.PlayersData;
import com.tenisdle.model.Ranking;
import com.tenisdle.model.RankingsData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * Cliente de AcesAPI. Todas las peticiones son POST a /v1/{herramienta}
 * con los parametros en el body y la API key en la cabecera x-api-key.
 * Ojo: cada peticion gasta creditos (500 al mes gratis).
 */
public class AcesApiClient {

    private HttpClient http = HttpClient.newHttpClient();
    private ObjectMapper mapper = JacksonConfig.crearMapper();
    private String url = AppConfig.getInstance().getApiUrl();
    private String apiKey = AppConfig.getInstance().getApiKey();

    // Ranking ATP actual (2 creditos)
    public List<Ranking> getRankings(int limit) throws IOException, InterruptedException {
        String json = post("get_rankings", Map.of("limit", limit));
        ApiResponse<RankingsData> respuesta = mapper.readValue(json, new TypeReference<ApiResponse<RankingsData>>() {});
        return respuesta.getData().getRankings();
    }

    // Jugadores de un pais, por ejemplo "ESP" (1 credito)
    public List<Player> buscarPorPais(String pais) throws IOException, InterruptedException {
        String json = post("search_players", Map.of("country", pais, "limit", 100));
        ApiResponse<PlayersData> respuesta = mapper.readValue(json, new TypeReference<ApiResponse<PlayersData>>() {});
        return respuesta.getData().getPlayers();
    }

    // Jugadores por nombre, minimo 2 letras (1 credito)
    public List<Player> buscarPorNombre(String nombre) throws IOException, InterruptedException {
        String json = post("search_players", Map.of("name", nombre));
        ApiResponse<PlayersData> respuesta = mapper.readValue(json, new TypeReference<ApiResponse<PlayersData>>() {});
        return respuesta.getData().getPlayers();
    }

    // Hace el POST y devuelve el JSON de respuesta
    private String post(String herramienta, Map<String, Object> parametros) throws IOException, InterruptedException {
        String body = mapper.writeValueAsString(parametros);

        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(url + herramienta))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString());

        // Si no es 200 la API devuelve algo como {"error": "..."}
        if (respuesta.statusCode() != 200) {
            throw new IOException("Error " + respuesta.statusCode() + ": " + respuesta.body());
        }
        return respuesta.body();
    }
}
