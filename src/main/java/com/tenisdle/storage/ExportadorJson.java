package com.tenisdle.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenisdle.config.JacksonConfig;
import com.tenisdle.model.Tenista;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Exporta los tenistas a un fichero JSON con Jackson.
 */
public class ExportadorJson {

    private ObjectMapper mapper = JacksonConfig.crearMapper();

    // Si el fichero no existe se crea y si existe se sobreescribe
    public String exportar(List<Tenista> tenistas, String nombreFichero) throws IOException {
        if (!nombreFichero.endsWith(".json")) {
            nombreFichero = nombreFichero + ".json";
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(nombreFichero), tenistas);
        return nombreFichero;
    }
}
