package com.tenisdle.service;

import com.tenisdle.model.Intento;
import com.tenisdle.model.Pista;
import com.tenisdle.model.Resultado;
import com.tenisdle.model.Tenista;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JuegoServiceTest {

    private final RepositoriosEnMemoria.Partidas partidas = new RepositoriosEnMemoria.Partidas();

    private static Tenista tenista(String id, String pais, int edad, String mano, int ranking, int puntos) {
        return new Tenista(id, "Tenista " + id, pais, LocalDate.now().minusYears(edad), mano, ranking, puntos);
    }

    // Con un solo tenista en la BD el secreto es siempre ese
    private JuegoService juegoConSecreto(Tenista secreto) {
        JuegoService juego = new JuegoService(new RepositoriosEnMemoria.Tenistas(secreto), partidas);
        juego.nuevaPartida();
        return juego;
    }

    @Test
    void aciertaYGuardaLaPartida() {
        Tenista secreto = tenista("1", "ESP", 22, Tenista.DIESTRO, 1, 10000);
        JuegoService juego = juegoConSecreto(secreto);

        Intento intento = juego.intentar(secreto);

        assertTrue(intento.correcto());
        assertTrue(intento.pistas().stream().allMatch(p -> p.resultado() == Resultado.ACIERTO));
        assertTrue(juego.isTerminada());
        assertTrue(juego.isGanada());
        assertEquals(1, partidas.datos.size());
        assertEquals(1, partidas.datos.get(0).getIntentos());
    }

    @Test
    void daLasPistasCorrectas() {
        Tenista secreto = tenista("1", "ESP", 22, Tenista.DIESTRO, 5, 5000);
        JuegoService juego = juegoConSecreto(secreto);

        // Mas mayor, zurdo, mejor ranking y con mas puntos que el secreto
        Intento intento = juego.intentar(tenista("2", "ITA", 30, Tenista.ZURDO, 1, 9000));
        List<Resultado> resultados = intento.pistas().stream().map(Pista::resultado).toList();

        assertFalse(intento.correcto());
        assertEquals(List.of(Resultado.FALLO, Resultado.MENOR, Resultado.FALLO, Resultado.MAYOR, Resultado.MENOR),
                resultados);
        assertFalse(juego.isTerminada());
        assertEquals(JuegoService.MAX_INTENTOS - 1, juego.getIntentosRestantes());
    }

    @Test
    void noDejaRepetirTenista() {
        JuegoService juego = juegoConSecreto(tenista("1", "ESP", 22, Tenista.DIESTRO, 1, 10000));
        Tenista otro = tenista("2", "ITA", 30, Tenista.ZURDO, 2, 9000);

        juego.intentar(otro);

        assertThrows(IllegalArgumentException.class, () -> juego.intentar(otro));
    }

    @Test
    void pierdeAlAgotarLosIntentos() {
        JuegoService juego = juegoConSecreto(tenista("0", "ESP", 22, Tenista.DIESTRO, 1, 10000));

        for (int i = 1; i <= JuegoService.MAX_INTENTOS; i++) {
            juego.intentar(tenista(String.valueOf(i), "ITA", 30, Tenista.ZURDO, i + 1, 100));
        }

        assertTrue(juego.isTerminada());
        assertFalse(juego.isGanada());
        assertFalse(partidas.datos.get(0).isGanada());
        assertThrows(IllegalStateException.class,
                () -> juego.intentar(tenista("99", "ITA", 30, Tenista.ZURDO, 99, 1)));
    }

    @Test
    void fallaSiNoHayTenistas() {
        JuegoService juego = new JuegoService(new RepositoriosEnMemoria.Tenistas(), partidas);
        assertThrows(IllegalStateException.class, juego::nuevaPartida);
    }

    @Test
    void calculaLasEstadisticas() {
        Tenista secreto = tenista("1", "ESP", 22, Tenista.DIESTRO, 1, 10000);
        JuegoService juego = juegoConSecreto(secreto);

        // Ganada a la primera
        juego.intentar(secreto);
        // Ganada a la segunda
        juego.nuevaPartida();
        juego.intentar(tenista("2", "ITA", 30, Tenista.ZURDO, 2, 9000));
        juego.intentar(secreto);
        // Perdida
        juego.nuevaPartida();
        for (int i = 0; i < JuegoService.MAX_INTENTOS; i++) {
            juego.intentar(tenista("f" + i, "FRA", 25, Tenista.DIESTRO, 10 + i, 100));
        }

        assertEquals(3, juego.getHistorial().size());
        assertEquals(200.0 / 3, juego.porcentajeVictorias(), 0.001);
        assertEquals(1.5, juego.mediaIntentos(), 0.001);
    }
}
