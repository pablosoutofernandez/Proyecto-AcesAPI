package com.tenisdle.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Lee la configuracion de config.properties (API y base de datos).
 * Primero busca el fichero junto al JAR / en la carpeta del proyecto
 * y si no lo encuentra lo busca en resources.
 * Se usa como singleton para leer el fichero una sola vez.
 */
public class AppConfig {

    private static final String FICHERO = "config.properties";
    private static AppConfig instancia;

    private final Properties props = new Properties();

    private AppConfig() {
        Path ruta = Path.of(FICHERO);
        try (InputStream in = Files.exists(ruta)
                ? Files.newInputStream(ruta)
                : AppConfig.class.getClassLoader().getResourceAsStream(FICHERO)) {
            if (in == null) {
                throw new IllegalStateException("No se encuentra " + FICHERO
                        + ". Copia config.properties.example como config.properties y rellenalo.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Error leyendo " + FICHERO, e);
        }
    }

    public static synchronized AppConfig getInstance() {
        if (instancia == null) {
            instancia = new AppConfig();
        }
        return instancia;
    }

    public String getApiUrl() {
        String url = props.getProperty("api.url", "https://mcp.acesapi.com/v1/");
        return url.endsWith("/") ? url : url + "/";
    }

    public String getApiKey() {
        return props.getProperty("api.key", "");
    }

    public int getNumTenistas() {
        return Integer.parseInt(props.getProperty("api.num_tenistas", "100").trim());
    }

    public String getDbUrl() {
        return props.getProperty("db.url");
    }

    public String getDbUser() {
        return props.getProperty("db.user");
    }

    public String getDbPassword() {
        return props.getProperty("db.password", "");
    }
}
