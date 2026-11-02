package com.tenisdle.model;

import java.util.List;

/**
 * Un intento de la partida: el tenista que ha elegido el usuario
 * y la lista de pistas (una por atributo).
 *
 * @param tenista  tenista elegido
 * @param pistas   pistas en el orden en que se muestran
 * @param correcto true si el tenista elegido es el secreto
 */
public record Intento(Tenista tenista, List<Pista> pistas, boolean correcto) {
}
