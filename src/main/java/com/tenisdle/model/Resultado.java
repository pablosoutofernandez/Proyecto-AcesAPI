package com.tenisdle.model;

/**
 * Resultado de comparar un atributo del intento con el del tenista secreto.
 */
public enum Resultado {
    /** El valor coincide (casilla verde). */
    ACIERTO,
    /** No coincide y no se puede comparar por tamano (pais, mano). */
    FALLO,
    /** El valor del tenista secreto es MAYOR que el del intento (flecha arriba). */
    MAYOR,
    /** El valor del tenista secreto es MENOR que el del intento (flecha abajo). */
    MENOR
}
