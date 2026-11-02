package com.tenisdle.service;

import com.tenisdle.model.Tenista;
import com.tenisdle.repository.TenistaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CRUD de tenistas y consultas con streams (busquedas, filtros, ordenaciones).
 */
public class TenistaService {

    private TenistaRepository repo = new TenistaRepository();

    public void crear(Tenista t) {
        validar(t);
        if (repo.buscarPorId(t.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un tenista con id " + t.getId());
        }
        repo.insertar(t);
    }

    public Tenista leer(String id) {
        return repo.buscarPorId(id);
    }

    public boolean modificar(Tenista t) {
        validar(t);
        return repo.actualizar(t);
    }

    public boolean borrar(String id) {
        return repo.borrar(id);
    }

    public List<Tenista> listarTodos() {
        return ordenarPorRanking();
    }

    // Tenistas cuyo nombre contiene el texto, sin importar mayusculas
    public List<Tenista> buscarPorNombre(String texto) {
        return repo.buscarTodos().stream()
                .filter(t -> t.getNombre().toLowerCase().contains(texto.toLowerCase()))
                .sorted(Comparator.comparing(Tenista::getRanking))
                .toList();
    }

    // Para el buscador del juego, muestra como mucho 'maximo' sugerencias
    public List<Tenista> sugerencias(String texto, int maximo) {
        if (texto.isBlank()) {
            return List.of();
        }
        return buscarPorNombre(texto).stream()
                .limit(maximo)
                .toList();
    }

    public List<Tenista> filtrarPorPais(String pais) {
        return repo.buscarTodos().stream()
                .filter(t -> t.getPais().equalsIgnoreCase(pais))
                .sorted(Comparator.comparing(Tenista::getRanking))
                .toList();
    }

    public List<Tenista> filtrarPorMano(String mano) {
        return repo.buscarTodos().stream()
                .filter(t -> t.getMano().equalsIgnoreCase(mano))
                .sorted(Comparator.comparing(Tenista::getRanking))
                .toList();
    }

    public List<Tenista> top(int n) {
        return ordenarPorRanking().stream()
                .limit(n)
                .toList();
    }

    public List<Tenista> ordenarPorRanking() {
        return repo.buscarTodos().stream()
                .sorted(Comparator.comparing(Tenista::getRanking))
                .toList();
    }

    public List<Tenista> ordenarPorNombre() {
        return repo.buscarTodos().stream()
                .sorted(Comparator.comparing(Tenista::getNombre))
                .toList();
    }

    // Del mas joven al mas mayor
    public List<Tenista> ordenarPorEdad() {
        return repo.buscarTodos().stream()
                .sorted(Comparator.comparing(Tenista::getEdad))
                .toList();
    }

    // De mas puntos a menos
    public List<Tenista> ordenarPorPuntos() {
        return repo.buscarTodos().stream()
                .sorted(Comparator.comparing(Tenista::getPuntos).reversed())
                .toList();
    }

    // Lista de paises sin repetir, para un ComboBox
    public List<String> listarPaises() {
        return repo.buscarTodos().stream()
                .map(Tenista::getPais)
                .distinct()
                .sorted()
                .toList();
    }

    public Map<String, Long> contarPorPais() {
        return repo.buscarTodos().stream()
                .collect(Collectors.groupingBy(Tenista::getPais, Collectors.counting()));
    }

    public double edadMedia() {
        return repo.buscarTodos().stream()
                .mapToInt(Tenista::getEdad)
                .average()
                .orElse(0);
    }

    public Tenista masJoven() {
        return repo.buscarTodos().stream()
                .min(Comparator.comparing(Tenista::getEdad))
                .orElse(null);
    }

    public Tenista masMayor() {
        return repo.buscarTodos().stream()
                .max(Comparator.comparing(Tenista::getEdad))
                .orElse(null);
    }

    public long contarZurdos() {
        return repo.buscarTodos().stream()
                .filter(t -> t.getMano().equals(Tenista.ZURDO))
                .count();
    }

    // Comprueba los datos antes de guardar, si algo esta mal lanza una excepcion con el mensaje
    private void validar(Tenista t) {
        if (t.getId() == null || t.getId().isBlank()) {
            throw new IllegalArgumentException("El id es obligatorio");
        }
        if (t.getNombre() == null || t.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (t.getPais() == null || t.getPais().length() != 3) {
            throw new IllegalArgumentException("El pais tiene que tener 3 letras (ESP, ITA...)");
        }
        if (t.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (!t.getMano().equals(Tenista.DIESTRO) && !t.getMano().equals(Tenista.ZURDO)) {
            throw new IllegalArgumentException("La mano tiene que ser Diestro o Zurdo");
        }
        if (t.getRanking() < 1) {
            throw new IllegalArgumentException("El ranking tiene que ser mayor que 0");
        }
    }
}
