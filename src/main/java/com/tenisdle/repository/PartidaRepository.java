package com.tenisdle.repository;

import com.tenisdle.database.BaseDatosException;
import com.tenisdle.database.ConexionBD;
import com.tenisdle.model.Partida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda y lee el historial de partidas.
 */
public class PartidaRepository {

    public void insertar(Partida p) {
        String sql = "INSERT INTO partidas (tenista_id, fecha, intentos, ganada) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getTenistaId());
            ps.setTimestamp(2, Timestamp.valueOf(p.getFecha()));
            ps.setInt(3, p.getIntentos());
            ps.setBoolean(4, p.isGanada());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error al guardar la partida", e);
        }
    }

    public List<Partida> buscarTodas() {
        List<Partida> lista = new ArrayList<>();
        String sql = "SELECT * FROM partidas ORDER BY fecha DESC";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Partida p = new Partida();
                p.setId(rs.getInt("id"));
                p.setTenistaId(rs.getString("tenista_id"));
                p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                p.setIntentos(rs.getInt("intentos"));
                p.setGanada(rs.getBoolean("ganada"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error al leer las partidas", e);
        }
        return lista;
    }

    public void borrarTodas() {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM partidas")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error al borrar las partidas", e);
        }
    }
}
