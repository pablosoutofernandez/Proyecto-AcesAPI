package com.tenisdle.storage;

import com.tenisdle.model.Tenista;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportadorCsvTest {

    @TempDir
    Path dir;

    @Test
    void exportaCabeceraYUnaLineaPorTenista() throws Exception {
        Tenista t = new Tenista("1", "Carlos Alcaraz", "ESP", LocalDate.of(2003, 5, 5), Tenista.DIESTRO, 2, 9000);

        String fichero = new ExportadorCsv().exportar(List.of(t), dir.resolve("tenistas").toString());

        assertTrue(fichero.endsWith(".csv"));
        List<String> lineas = Files.readAllLines(Path.of(fichero));
        assertEquals(2, lineas.size());
        assertEquals("id;nombre;pais;fecha_nacimiento;edad;mano;ranking;puntos", lineas.get(0));
        assertTrue(lineas.get(1).startsWith("1;Carlos Alcaraz;ESP;2003-05-05;"));
        assertTrue(lineas.get(1).endsWith(";Diestro;2;9000"));
    }
}
