package com.tenisdle.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Tenista que se guarda en la base de datos y con el que se juega.
 * Junta los datos del ranking (puntos, posicion) con los de search_players
 * (fecha de nacimiento y mano).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tenista {

    public static final String DIESTRO = "Diestro";
    public static final String ZURDO = "Zurdo";

    private String id;
    private String nombre;
    private String pais;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    private String mano;
    private int ranking;
    private int puntos;

    public Tenista() {
    }

    public Tenista(String id, String nombre, String pais, LocalDate fechaNacimiento,
                   String mano, int ranking, int puntos) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.fechaNacimiento = fechaNacimiento;
        this.mano = mano;
        this.ranking = ranking;
        this.puntos = puntos;
    }

    /**
     * Crea un tenista a partir de su fila del ranking y de sus datos de search_players.
     */
    public static Tenista desde(Ranking ranking, Player player) {
        LocalDate fecha = player.getDateOfBirth() == null ? null : LocalDate.parse(player.getDateOfBirth());
        return new Tenista(ranking.getPlayerId(), ranking.getName(), ranking.getCountry(),
                fecha, traducirMano(player.getPlays()), ranking.getRank(), ranking.getPoints());
    }

    /**
     * La API devuelve "right" / "left", en la app se muestra en castellano.
     */
    public static String traducirMano(String plays) {
        if (plays == null) {
            return null;
        }
        return switch (plays.toLowerCase()) {
            case "right" -> DIESTRO;
            case "left" -> ZURDO;
            default -> null;
        };
    }

    /**
     * Edad calculada a dia de hoy (no se guarda en la BD).
     */
    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getMano() {
        return mano;
    }

    public void setMano(String mano) {
        this.mano = mano;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    // Dos tenistas son iguales si tienen el mismo id de la API
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tenista tenista)) return false;
        return Objects.equals(id, tenista.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre + " (" + pais + ", #" + ranking + ")";
    }
}
