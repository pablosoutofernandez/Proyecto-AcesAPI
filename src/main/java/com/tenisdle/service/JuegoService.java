package com.tenisdle.service;

import com.tenisdle.model.Intento;
import com.tenisdle.model.Partida;
import com.tenisdle.model.Pista;
import com.tenisdle.model.Resultado;
import com.tenisdle.model.Tenista;
import com.tenisdle.repository.PartidaRepository;
import com.tenisdle.repository.TenistaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Logica del juego (tipo Wordle). Se elige un tenista secreto al azar
 * y el usuario tiene MAX_INTENTOS para adivinarlo. En cada intento se
 * comparan pais, edad, mano, ranking y puntos.
 */
public class JuegoService {

    public static final int MAX_INTENTOS = 8;

    private TenistaRepository tenistaRepo;
    private PartidaRepository partidaRepo;

    private Tenista secreto;
    private List<Intento> intentos = new ArrayList<>();
    private boolean terminada = true;

    public JuegoService() {
        this(new TenistaRepository(), new PartidaRepository());
    }

    // Permite pasar repositorios falsos en los tests
    JuegoService(TenistaRepository tenistaRepo, PartidaRepository partidaRepo) {
        this.tenistaRepo = tenistaRepo;
        this.partidaRepo = partidaRepo;
    }

    public void nuevaPartida() {
        List<Tenista> tenistas = tenistaRepo.buscarTodos();
        if (tenistas.isEmpty()) {
            throw new IllegalStateException("No hay tenistas en la base de datos");
        }
        secreto = tenistas.get(new Random().nextInt(tenistas.size()));
        intentos.clear();
        terminada = false;
    }

    public Intento intentar(Tenista elegido) {
        if (terminada) {
            throw new IllegalStateException("La partida ha terminado");
        }
        boolean repetido = intentos.stream().anyMatch(i -> i.tenista().equals(elegido));
        if (repetido) {
            throw new IllegalArgumentException("Ya has probado con " + elegido.getNombre());
        }

        boolean correcto = elegido.equals(secreto);
        Intento intento = new Intento(elegido, comparar(elegido), correcto);
        intentos.add(intento);

        // Si acierta o se queda sin intentos se acaba y se guarda en la BD
        if (correcto || intentos.size() == MAX_INTENTOS) {
            terminada = true;
            partidaRepo.insertar(new Partida(secreto.getId(), LocalDateTime.now(), intentos.size(), correcto));
        }
        return intento;
    }

    // Compara el tenista elegido con el secreto y devuelve las 5 pistas
    private List<Pista> comparar(Tenista t) {
        List<Pista> pistas = new ArrayList<>();
        pistas.add(compararTexto("Pais", t.getPais(), secreto.getPais()));
        pistas.add(compararNumero("Edad", t.getEdad(), secreto.getEdad()));
        pistas.add(compararTexto("Mano", t.getMano(), secreto.getMano()));
        pistas.add(compararNumero("Ranking", t.getRanking(), secreto.getRanking()));
        pistas.add(compararNumero("Puntos", t.getPuntos(), secreto.getPuntos()));
        return pistas;
    }

    private Pista compararTexto(String atributo, String valor, String valorSecreto) {
        if (valor.equals(valorSecreto)) {
            return new Pista(atributo, valor, Resultado.ACIERTO);
        }
        return new Pista(atributo, valor, Resultado.FALLO);
    }

    // MAYOR = el del secreto es mas alto, MENOR = el del secreto es mas bajo
    private Pista compararNumero(String atributo, int valor, int valorSecreto) {
        Resultado r;
        if (valor == valorSecreto) {
            r = Resultado.ACIERTO;
        } else if (valorSecreto > valor) {
            r = Resultado.MAYOR;
        } else {
            r = Resultado.MENOR;
        }
        return new Pista(atributo, String.valueOf(valor), r);
    }

    public List<Intento> getIntentos() {
        return intentos;
    }

    public int getIntentosRestantes() {
        return MAX_INTENTOS - intentos.size();
    }

    public boolean isTerminada() {
        return terminada;
    }

    public boolean isGanada() {
        return intentos.stream().anyMatch(Intento::correcto);
    }

    public Tenista getSecreto() {
        return secreto;
    }

    // Historial y estadisticas de las partidas guardadas

    public List<Partida> getHistorial() {
        return partidaRepo.buscarTodas();
    }

    public double porcentajeVictorias() {
        List<Partida> partidas = partidaRepo.buscarTodas();
        if (partidas.isEmpty()) {
            return 0;
        }
        long ganadas = partidas.stream().filter(Partida::isGanada).count();
        return ganadas * 100.0 / partidas.size();
    }

    public double mediaIntentos() {
        return partidaRepo.buscarTodas().stream()
                .filter(Partida::isGanada)
                .mapToInt(Partida::getIntentos)
                .average()
                .orElse(0);
    }

    public void borrarHistorial() {
        partidaRepo.borrarTodas();
    }
}
