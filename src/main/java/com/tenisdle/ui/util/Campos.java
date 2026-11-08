package com.tenisdle.ui.util;

import javafx.scene.control.Spinner;

/**
 * Ayudas para leer campos de formularios.
 */
public final class Campos {

    private Campos() {
    }

    /**
     * Lee el numero de un Spinner editable. Si el usuario escribe sin pulsar Enter,
     * el Spinner aun no ha guardado el valor: se lee lo escrito y, si no es un numero,
     * se usa el ultimo valor valido.
     */
    public static int leerNumero(Spinner<Integer> spinner) {
        try {
            return Integer.parseInt(spinner.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            return spinner.getValue();
        }
    }
}
