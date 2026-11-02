package com.tenisdle.model;

/**
 * Una casilla de la fila de un intento: que atributo es, que valor tiene
 * el tenista elegido y como se compara con el secreto.
 *
 * @param atributo  nombre de la columna ("Pais", "Edad"...)
 * @param valor     valor del tenista del intento, ya en texto para mostrarlo
 * @param resultado comparacion con el tenista secreto
 */
public record Pista(String atributo, String valor, Resultado resultado) {
}
