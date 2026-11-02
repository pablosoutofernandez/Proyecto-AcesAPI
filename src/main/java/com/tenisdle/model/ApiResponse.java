package com.tenisdle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Envoltorio comun de todas las respuestas de AcesAPI.
 * La API siempre devuelve el contenido dentro de un objeto "data".
 *
 * @param <T> tipo del contenido de "data" (PlayersData, RankingsData...)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {

    private T data;

    public ApiResponse() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
