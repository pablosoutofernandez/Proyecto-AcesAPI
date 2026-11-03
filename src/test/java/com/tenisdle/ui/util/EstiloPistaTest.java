package com.tenisdle.ui.util;

import com.tenisdle.model.Pista;
import com.tenisdle.model.Resultado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstiloPistaTest {

    @Test
    void cadaResultadoTieneSuClaseCss() {
        assertEquals("acierto", EstiloPista.claseCss(Resultado.ACIERTO));
        assertEquals("fallo", EstiloPista.claseCss(Resultado.FALLO));
        assertEquals("mayor", EstiloPista.claseCss(Resultado.MAYOR));
        assertEquals("menor", EstiloPista.claseCss(Resultado.MENOR));
    }

    @Test
    void mayorLlevaFlechaArriba() {
        assertEquals("25 ↑", EstiloPista.texto(new Pista("Edad", "25", Resultado.MAYOR)));
    }

    @Test
    void menorLlevaFlechaAbajo() {
        assertEquals("25 ↓", EstiloPista.texto(new Pista("Edad", "25", Resultado.MENOR)));
    }

    @Test
    void aciertoYFalloSinFlecha() {
        assertEquals("ESP", EstiloPista.texto(new Pista("Pais", "ESP", Resultado.ACIERTO)));
        assertEquals("Zurdo", EstiloPista.texto(new Pista("Mano", "Zurdo", Resultado.FALLO)));
    }
}
