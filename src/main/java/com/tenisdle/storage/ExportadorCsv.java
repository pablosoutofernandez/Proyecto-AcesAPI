package com.tenisdle.storage;

import com.tenisdle.model.Tenista;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Exporta los tenistas a un fichero CSV separado por ;
 */
public class ExportadorCsv {

    // Si el fichero no existe se crea y si existe se sobreescribe
    public String exportar(List<Tenista> tenistas, String nombreFichero) throws IOException {
        if (!nombreFichero.endsWith(".csv")) {
            nombreFichero = nombreFichero + ".csv";
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreFichero))) {
            pw.println("id;nombre;pais;fecha_nacimiento;edad;mano;ranking;puntos");
            tenistas.stream()
                    .map(t -> t.getId() + ";" + t.getNombre() + ";" + t.getPais() + ";"
                            + t.getFechaNacimiento() + ";" + t.getEdad() + ";" + t.getMano() + ";"
                            + t.getRanking() + ";" + t.getPuntos())
                    .forEach(pw::println);
        }
        return nombreFichero;
    }
}
