package com.tenisdle.model;

import java.time.LocalDateTime;

/**
 * Partida terminada del juego. Se guarda en la BD para el historial.
 * Cada partida tiene un tenista secreto (relacion N:1 con Tenista).
 */
public class Partida {

    private int id;
    private String tenistaId;
    private LocalDateTime fecha;
    private int intentos;
    private boolean ganada;

    public Partida() {
    }

    public Partida(String tenistaId, LocalDateTime fecha, int intentos, boolean ganada) {
        this.tenistaId = tenistaId;
        this.fecha = fecha;
        this.intentos = intentos;
        this.ganada = ganada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenistaId() {
        return tenistaId;
    }

    public void setTenistaId(String tenistaId) {
        this.tenistaId = tenistaId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public boolean isGanada() {
        return ganada;
    }

    public void setGanada(boolean ganada) {
        this.ganada = ganada;
    }

    @Override
    public String toString() {
        return "Partida{" +
                "id=" + id +
                ", tenistaId='" + tenistaId + '\'' +
                ", fecha=" + fecha +
                ", intentos=" + intentos +
                ", ganada=" + ganada +
                '}';
    }
}
