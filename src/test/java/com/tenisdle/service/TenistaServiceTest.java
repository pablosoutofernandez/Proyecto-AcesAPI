package com.tenisdle.service;

import com.tenisdle.model.Tenista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TenistaServiceTest {

    private TenistaService service;

    private final Tenista alcaraz = new Tenista("1", "Carlos Alcaraz", "ESP", LocalDate.of(2003, 5, 5), Tenista.DIESTRO, 2, 9000);
    private final Tenista sinner = new Tenista("2", "Jannik Sinner", "ITA", LocalDate.of(2001, 8, 16), Tenista.DIESTRO, 1, 11000);
    private final Tenista shelton = new Tenista("3", "Ben Shelton", "USA", LocalDate.of(2002, 10, 9), Tenista.ZURDO, 10, 3000);

    @BeforeEach
    void setUp() {
        service = new TenistaService(new RepositoriosEnMemoria.Tenistas(alcaraz, sinner, shelton));
    }

    @Test
    void buscaPorNombreSinImportarMayusculas() {
        assertEquals(List.of(alcaraz), service.buscarPorNombre("ALCA"));
        assertTrue(service.sugerencias("  ", 5).isEmpty());
    }

    @Test
    void ordenaYFiltra() {
        assertEquals(List.of(sinner, alcaraz, shelton), service.ordenarPorRanking());
        assertEquals(List.of(sinner, alcaraz, shelton), service.ordenarPorPuntos());
        assertEquals(List.of(sinner, alcaraz), service.top(2));
        assertEquals(List.of(shelton), service.filtrarPorMano(Tenista.ZURDO));
        assertEquals(List.of("ESP", "ITA", "USA"), service.listarPaises());
        assertEquals(1, service.contarZurdos());
    }

    @Test
    void crearValidaLosDatos() {
        Tenista paisMal = new Tenista("4", "X", "ES", LocalDate.of(2000, 1, 1), Tenista.DIESTRO, 5, 100);
        Tenista rankingMal = new Tenista("4", "X", "ESP", LocalDate.of(2000, 1, 1), Tenista.DIESTRO, 0, 100);
        Tenista repetido = new Tenista("1", "X", "ESP", LocalDate.of(2000, 1, 1), Tenista.DIESTRO, 5, 100);

        assertThrows(IllegalArgumentException.class, () -> service.crear(paisMal));
        assertThrows(IllegalArgumentException.class, () -> service.crear(rankingMal));
        assertThrows(IllegalArgumentException.class, () -> service.crear(repetido));
    }

    @Test
    void crudBasico() {
        Tenista nuevo = new Tenista("4", "Rafa Nadal", "ESP", LocalDate.of(1986, 6, 3), Tenista.ZURDO, 50, 500);
        service.crear(nuevo);
        assertEquals(nuevo, service.leer("4"));

        assertTrue(service.borrar("4"));
        assertNull(service.leer("4"));
        assertFalse(service.borrar("4"));
    }
}
