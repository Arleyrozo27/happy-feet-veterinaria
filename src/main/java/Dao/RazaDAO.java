package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Raza.Raza;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class RazaDAO implements BaseDAO<Raza> {
    
    @Override
    public Optional<Raza> encontrarPorId(int id) {
        String sql = "SELECT r.*, e.nombre as especie_nombre FROM razas r JOIN especies e ON r.especie_id = e.id WHERE r.id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearRaza(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar raza por ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Raza> encontrarTodos() {
        List<Raza> razas = new ArrayList<>();
        String sql = "SELECT r.*, e.nombre as especie_nombre FROM razas r JOIN especies e ON r.especie_id = e.id ORDER BY e.nombre, r.nombre";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                razas.add(mapearRaza(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener todas las razas", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return razas;
    }
    
    @Override
    public int guardar(Raza raza) {
        // No necesario para este proyecto
        return -1;
    }
    
    @Override
    public boolean actualizar(Raza raza) {
        // No necesario para este proyecto
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        // No necesario para este proyecto
        return false;
    }
    
    public List<Raza> encontrarPorEspecie(int especieId) {
        List<Raza> razas = new ArrayList<>();
        String sql = "SELECT r.*, e.nombre as especie_nombre FROM razas r JOIN especies e ON r.especie_id = e.id WHERE r.especie_id = ? ORDER BY r.nombre";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, especieId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                razas.add(mapearRaza(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar razas por especie: " + especieId, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return razas;
    }
    
    private Raza mapearRaza(ResultSet rs) throws SQLException {
        Raza raza = new Raza();
        raza.setId(rs.getInt("id"));
        raza.setEspecieId(rs.getInt("especie_id"));
        raza.setNombre(rs.getString("nombre"));
        raza.setCaracteristicas(rs.getString("caracteristicas"));
        raza.setEspecieNombre(rs.getString("especie_nombre"));
        return raza;
    }
    
    private void cerrarRecursos(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LoggerManager.logError("Error al cerrar recursos", e);
        }
    }
}
