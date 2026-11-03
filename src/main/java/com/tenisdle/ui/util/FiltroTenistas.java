package com.tenisdle.ui.util;

import com.tenisdle.model.Tenista;

import java.util.function.Predicate;

/**
 * Filtro del buscador de la partida. Se aplica sobre la lista de tenistas
 * que ya esta en memoria para no consultar la BD en cada tecla.
 */
public final class FiltroTenistas {

    private FiltroTenistas() {
    }

    /**
     * Tenistas cuyo nombre contiene el texto, sin importar mayusculas.
     * Con el texto vacio no pasa ninguno (no se muestran sugerencias).
     */
    public static Predicate<Tenista> porNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            return t -> false;
        }
        String buscado = texto.trim().toLowerCase();
        return t -> t.getNombre().toLowerCase().contains(buscado);
    }
}
