package Dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Veterinario.Veterinario;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class VeterinarioDAO implements BaseDAO<Veterinario> {
    
    @Override
    public Optional<Veterinario> encontrarPorId(int id) {
        String sql = "SELECT * FROM veterinarios WHERE id = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearVeterinario(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar veterinario por ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Veterinario> encontrarTodos() {
        List<Veterinario> veterinarios = new ArrayList<>();
        String sql = "SELECT * FROM veterinarios WHERE activo = TRUE ORDER BY nombre_completo";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                veterinarios.add(mapearVeterinario(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener todos los veterinarios", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return veterinarios;
    }
    
    @Override
    public int guardar(Veterinario veterinario) {
        String sql = "INSERT INTO veterinarios (nombre_completo, documento_identidad, licencia_profesional, especialidad, telefono, email, fecha_contratacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, veterinario.getNombreCompleto());
            stmt.setString(2, veterinario.getDocumentoIdentidad());
            stmt.setString(3, veterinario.getLicenciaProfesional());
            stmt.setString(4, veterinario.getEspecialidad());
            stmt.setString(5, veterinario.getTelefono());
            stmt.setString(6, veterinario.getEmail());
            
            if (veterinario.getFechaContratacion() != null) {
                stmt.setDate(7, Date.valueOf(veterinario.getFechaContratacion()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al guardar veterinario: " + veterinario.getDocumentoIdentidad(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return -1;
    }
    
    @Override
    public boolean actualizar(Veterinario veterinario) {
        String sql = "UPDATE veterinarios SET nombre_completo = ?, especialidad = ?, telefono = ?, email = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, veterinario.getNombreCompleto());
            stmt.setString(2, veterinario.getEspecialidad());
            stmt.setString(3, veterinario.getTelefono());
            stmt.setString(4, veterinario.getEmail());
            stmt.setInt(5, veterinario.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al actualizar veterinario ID: " + veterinario.getId(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE veterinarios SET activo = FALSE WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al eliminar veterinario ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    private Veterinario mapearVeterinario(ResultSet rs) throws SQLException {
        Veterinario veterinario = new Veterinario();
        veterinario.setId(rs.getInt("id"));
        veterinario.setNombreCompleto(rs.getString("nombre_completo"));
        veterinario.setDocumentoIdentidad(rs.getString("documento_identidad"));
        veterinario.setLicenciaProfesional(rs.getString("licencia_profesional"));
        veterinario.setEspecialidad(rs.getString("especialidad"));
        veterinario.setTelefono(rs.getString("telefono"));
        veterinario.setEmail(rs.getString("email"));
        
        Date fechaContratacion = rs.getDate("fecha_contratacion");
        if (fechaContratacion != null) {
            veterinario.setFechaContratacion(fechaContratacion.toLocalDate());
        }
        
        veterinario.setActivo(rs.getBoolean("activo"));
        return veterinario;
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