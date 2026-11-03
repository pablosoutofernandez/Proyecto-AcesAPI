package com.tenisdle.ui.util;

import com.tenisdle.model.Tenista;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FiltroTenistasTest {

    private final Tenista alcaraz = new Tenista("1", "Carlos Alcaraz", "ESP",
            LocalDate.of(2003, 5, 5), Tenista.DIESTRO, 1, 10000);

    @Test
    void encuentraPorParteDelNombreSinImportarMayusculas() {
        assertTrue(FiltroTenistas.porNombre("alca").test(alcaraz));
        assertTrue(FiltroTenistas.porNombre("CARLOS").test(alcaraz));
        assertTrue(FiltroTenistas.porNombre("  raz ").test(alcaraz));
    }

    @Test
    void noEncuentraSiNoCoincide() {
        assertFalse(FiltroTenistas.porNombre("sinner").test(alcaraz));
    }

    @Test
    void textoVacioNoMuestraNada() {
        Predicate<Tenista> vacio = FiltroTenistas.porNombre("   ");
        assertFalse(vacio.test(alcaraz));
        assertFalse(FiltroTenistas.porNombre(null).test(alcaraz));
    }
}
