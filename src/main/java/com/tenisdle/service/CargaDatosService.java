package com.tenisdle.service;

import com.tenisdle.config.AppConfig;
import com.tenisdle.model.Player;
import com.tenisdle.model.Ranking;
import com.tenisdle.model.Tenista;
import com.tenisdle.repository.AcesApiClient;
import com.tenisdle.repository.TenistaRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga de datos: la primera vez se descargan de la API (online)
 * y se guardan en MySQL. Las siguientes se leen de la BD (offline).
 */
public class CargaDatosService {

    private AcesApiClient api = new AcesApiClient();
    private TenistaRepository repo = new TenistaRepository();

    public boolean hayDatosEnBD() {
        return repo.contar() > 0;
    }

    public List<Tenista> cargar() throws IOException, InterruptedException {
        if (!hayDatosEnBD()) {
            descargarDeApi();
        }
        return repo.buscarTodos();
    }

    /**
     * get_rankings no trae la fecha de nacimiento ni la mano, asi que
     * buscamos los jugadores de cada pais del ranking y juntamos los datos por id.
     * Devuelve cuantos tenistas se han guardado.
     */
    public int descargarDeApi() throws IOException, InterruptedException {
        List<Ranking> ranking = api.getRankings(AppConfig.getInstance().getNumTenistas());

        List<String> paises = ranking.stream()
                .map(Ranking::getCountry)
                .distinct()
                .toList();

        // id del jugador -> datos del jugador
        Map<String, Player> jugadores = new HashMap<>();
        for (String pais : paises) {
            for (Player p : api.buscarPorPais(pais)) {
                jugadores.put(p.getPlayerId(), p);
            }
        }

        List<Tenista> tenistas = ranking.stream()
                .filter(r -> jugadores.containsKey(r.getPlayerId()))
                .map(r -> Tenista.desde(r, jugadores.get(r.getPlayerId())))
                .filter(t -> t.getFechaNacimiento() != null && t.getMano() != null)
                .toList();

        // Si ya estaba en la BD se actualiza, si no se inserta
        for (Tenista t : tenistas) {
            if (repo.buscarPorId(t.getId()) != null) {
                repo.actualizar(t);
            } else {
                repo.insertar(t);
            }
        }
        return tenistas.size();
    }
}
