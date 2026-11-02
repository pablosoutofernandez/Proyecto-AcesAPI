package com.tenisdle.repository;

import com.tenisdle.database.BaseDatosException;
import com.tenisdle.database.ConexionBD;
import com.tenisdle.model.Tenista;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD de la tabla tenistas.
 */
public class TenistaRepository {

    public void insertar(Tenista t) {
        String sql = "INSERT INTO tenistas (id, nombre, pais, fecha_nacimiento, mano, ranking, puntos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getId());
            ps.setString(2, t.getNombre());
            ps.setString(3, t.getPais());
            ps.setDate(4, Date.valueOf(t.getFechaNacimiento()));
            ps.setString(5, t.getMano());
            ps.setInt(6, t.getRanking());
            ps.setInt(7, t.getPuntos());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error al insertar " + t.getNombre(), e);
        }
    }

    public boolean actualizar(Tenista t) {
        String sql = "UPDATE tenistas SET nombre = ?, pais = ?, fecha_nacimiento = ?, mano = ?, ranking = ?, puntos = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getPais());
            ps.setDate(3, Date.valueOf(t.getFechaNacimiento()));
            ps.setString(4, t.getMano());
            ps.setInt(5, t.getRanking());
            ps.setInt(6, t.getPuntos());
            ps.setString(7, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BaseDatosException("Error al actualizar " + t.getNombre(), e);
        }
    }

    // Al borrar un tenista se borran tambien sus partidas (ON DELETE CASCADE)
    public boolean borrar(String id) {
        String sql = "DELETE FROM tenistas WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BaseDatosException("Error al borrar el tenista " + id, e);
        }
    }

    public List<Tenista> buscarTodos() {
        List<Tenista> lista = new ArrayList<>();
        String sql = "SELECT * FROM tenistas";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leerTenista(rs));
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error al leer los tenistas", e);
        }
        return lista;
    }

    // Devuelve null si no existe
    public Tenista buscarPorId(String id) {
        String sql = "SELECT * FROM tenistas WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return leerTenista(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar el tenista " + id, e);
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM tenistas";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new BaseDatosException("Error al contar los tenistas", e);
        }
    }

    // Pasa una fila del ResultSet a un objeto Tenista
    private Tenista leerTenista(ResultSet rs) throws SQLException {
        Tenista t = new Tenista();
        t.setId(rs.getString("id"));
        t.setNombre(rs.getString("nombre"));
        t.setPais(rs.getString("pais"));
        t.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        t.setMano(rs.getString("mano"));
        t.setRanking(rs.getInt("ranking"));
        t.setPuntos(rs.getInt("puntos"));
        return t;
    }
}
