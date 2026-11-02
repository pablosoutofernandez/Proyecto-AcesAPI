package com.tenisdle.service;

import com.tenisdle.model.Partida;
import com.tenisdle.model.Tenista;
import com.tenisdle.repository.PartidaRepository;
import com.tenisdle.repository.TenistaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorios que guardan en listas en vez de en MySQL, para los tests.
 */
class RepositoriosEnMemoria {

    static class Tenistas extends TenistaRepository {
        final List<Tenista> datos = new ArrayList<>();

        Tenistas(Tenista... tenistas) {
            datos.addAll(List.of(tenistas));
        }

        @Override
        public void insertar(Tenista t) {
            datos.add(t);
        }

        @Override
        public boolean actualizar(Tenista t) {
            boolean existe = datos.remove(t);
            if (existe) {
                datos.add(t);
            }
            return existe;
        }

        @Override
        public boolean borrar(String id) {
            return datos.removeIf(t -> t.getId().equals(id));
        }

        @Override
        public List<Tenista> buscarTodos() {
            return new ArrayList<>(datos);
        }

        @Override
        public Tenista buscarPorId(String id) {
            return datos.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public int contar() {
            return datos.size();
        }
    }

    static class Partidas extends PartidaRepository {
        final List<Partida> datos = new ArrayList<>();

        @Override
        public void insertar(Partida p) {
            datos.add(p);
        }

        @Override
        public List<Partida> buscarTodas() {
            return new ArrayList<>(datos);
        }

        @Override
        public void borrarTodas() {
            datos.clear();
        }
    }
}
