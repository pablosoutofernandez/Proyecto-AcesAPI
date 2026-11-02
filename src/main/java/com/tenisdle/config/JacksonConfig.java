package com.tenisdle.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Crea el ObjectMapper de Jackson con la configuracion comun del proyecto.
 */
public final class JacksonConfig {

    private JacksonConfig() {
    }

    public static ObjectMapper crearMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Para poder leer y escribir LocalDate / LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        // Las fechas se escriben como "2001-08-16" y no como [2001, 8, 16]
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
