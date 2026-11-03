package com.tenisdle.ui.util;

import com.tenisdle.model.Pista;
import com.tenisdle.model.Resultado;

/**
 * Como se pinta una pista: que clase CSS lleva su casilla y que texto se muestra.
 * No usa JavaFX para poder probarla con JUnit.
 */
public final class EstiloPista {

    public static final String FLECHA_ARRIBA = "↑";
    public static final String FLECHA_ABAJO = "↓";

    private EstiloPista() {
    }

    // Las clases estan definidas en estilos.css
    public static String claseCss(Resultado resultado) {
        return switch (resultado) {
            case ACIERTO -> "acierto";
            case FALLO -> "fallo";
            case MAYOR -> "mayor";
            case MENOR -> "menor";
        };
    }

    // MAYOR = el valor del secreto es mas alto, asi que la flecha apunta hacia arriba
    public static String texto(Pista pista) {
        return switch (pista.resultado()) {
            case MAYOR -> pista.valor() + " " + FLECHA_ARRIBA;
            case MENOR -> pista.valor() + " " + FLECHA_ABAJO;
            default -> pista.valor();
        };
    }
}
