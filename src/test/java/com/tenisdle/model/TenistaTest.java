package com.tenisdle.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TenistaTest {

    @Test
    void traduceLaMano() {
        assertEquals(Tenista.DIESTRO, Tenista.traducirMano("right"));
        assertEquals(Tenista.ZURDO, Tenista.traducirMano("Left"));
        assertNull(Tenista.traducirMano("ambidiestro"));
        assertNull(Tenista.traducirMano(null));
    }

    @Test
    void calculaLaEdad() {
        Tenista t = new Tenista("1", "A", "ESP", LocalDate.now().minusYears(25), Tenista.DIESTRO, 1, 100);
        assertEquals(25, t.getEdad());
    }

    @Test
    void edadCeroSinFechaDeNacimiento() {
        Tenista t = new Tenista("1", "A", "ESP", null, Tenista.DIESTRO, 1, 100);
        assertEquals(0, t.getEdad());
    }

    @Test
    void sonIgualesSiTienenElMismoId() {
        Tenista a = new Tenista("1", "A", "ESP", null, Tenista.DIESTRO, 1, 100);
        Tenista b = new Tenista("1", "B", "ITA", null, Tenista.ZURDO, 2, 50);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
