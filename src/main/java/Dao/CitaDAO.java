/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Cita.Cita;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class CitaDAO implements BaseDAO<Cita> {
    
    @Override
    public Optional<Cita> encontrarPorId(int id) {
        String sql = "SELECT * FROM citas WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearCita(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar cita por ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Cita> encontrarTodos() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM citas ORDER BY fecha_hora DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                citas.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener todas las citas", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return citas;
    }
    
    @Override
    public int guardar(Cita cita) {
        String sql = "INSERT INTO citas (mascota_id, veterinario_id, fecha_hora, motivo, estado_id, observaciones) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, cita.getMascotaId());
            
            if (cita.getVeterinarioId() != null) {
                stmt.setInt(2, cita.getVeterinarioId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            stmt.setString(4, cita.getMotivo());
            stmt.setInt(5, cita.getEstadoId());
            stmt.setString(6, cita.getObservaciones());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al guardar cita", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return -1;
    }
    
    @Override
    public boolean actualizar(Cita cita) {
        String sql = "UPDATE citas SET mascota_id = ?, veterinario_id = ?, fecha_hora = ?, motivo = ?, estado_id = ?, observaciones = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, cita.getMascotaId());
            
            if (cita.getVeterinarioId() != null) {
                stmt.setInt(2, cita.getVeterinarioId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            stmt.setString(4, cita.getMotivo());
            stmt.setInt(5, cita.getEstadoId());
            stmt.setString(6, cita.getObservaciones());
            stmt.setInt(7, cita.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al actualizar cita ID: " + cita.getId(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al eliminar cita ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    // MÉTODOS ESPECÍFICOS PARA CITAS
    
    /**
     * REGLA DE NEGOCIO CRÍTICA: Verificar si hay solapamiento de citas
     */
    public boolean existeSolapamiento(Integer veterinarioId, LocalDateTime fechaHora, int duracionMinutos) {
        String sql = "SELECT COUNT(*) FROM citas WHERE veterinario_id = ? AND estado_id IN (1,2) AND fecha_hora BETWEEN ? AND ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, veterinarioId);
            
            LocalDateTime inicio = fechaHora.minusMinutes(duracionMinutos);
            LocalDateTime fin = fechaHora.plusMinutes(duracionMinutos);
            
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fin));
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al verificar solapamiento de citas", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return false;
    }
    
    public List<Cita> encontrarPorMascota(int mascotaId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM citas WHERE mascota_id = ? ORDER BY fecha_hora DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mascotaId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                citas.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar citas de mascota ID: " + mascotaId, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return citas;
    }
    
    public List<Cita> encontrarPorVeterinario(int veterinarioId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM citas WHERE veterinario_id = ? ORDER BY fecha_hora DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, veterinarioId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                citas.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar citas de veterinario ID: " + veterinarioId, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return citas;
    }
    
    public List<Cita> encontrarPorEstado(int estadoId) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM citas WHERE estado_id = ? ORDER BY fecha_hora DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, estadoId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                citas.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar citas por estado: " + estadoId, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return citas;
    }
    
    public boolean actualizarEstado(int citaId, int nuevoEstadoId) {
        String sql = "UPDATE citas SET estado_id = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, nuevoEstadoId);
            stmt.setInt(2, citaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al actualizar estado de cita ID: " + citaId, e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    // Método privado para mapear ResultSet a objeto Cita
    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setId(rs.getInt("id"));
        cita.setMascotaId(rs.getInt("mascota_id"));
        
        int veterinarioId = rs.getInt("veterinario_id");
        if (!rs.wasNull()) {
            cita.setVeterinarioId(veterinarioId);
        }
        
        cita.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setMotivo(rs.getString("motivo"));
        cita.setEstadoId(rs.getInt("estado_id"));
        cita.setObservaciones(rs.getString("observaciones"));
        cita.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        
        return cita;
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
