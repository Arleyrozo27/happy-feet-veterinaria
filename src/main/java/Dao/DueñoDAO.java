package Dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Dueno.Dueño;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class DueñoDAO implements BaseDAO<Dueño> {
    
    @Override
    public Optional<Dueño> encontrarPorId(int id) {
        String sql = "SELECT * FROM duenos WHERE id = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearDueño(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar dueño por ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Dueño> encontrarTodos() {
        List<Dueño> dueños = new ArrayList<>();
        String sql = "SELECT * FROM duenos WHERE activo = TRUE ORDER BY id"; // ← CORREGIDO: ORDER BY id
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                dueños.add(mapearDueño(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener todos los dueños", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return dueños;
    }
    
    @Override
    public int guardar(Dueño dueño) {
        String sql = "INSERT INTO duenos (nombre_completo, documento_identidad, direccion, telefono, email, contacto_emergencia) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, dueño.getNombreCompleto());
            stmt.setString(2, dueño.getDocumentoIdentidad());
            stmt.setString(3, dueño.getDireccion());
            stmt.setString(4, dueño.getTelefono());
            stmt.setString(5, dueño.getEmail());
            stmt.setString(6, dueño.getContactoEmergencia());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al guardar dueno: " + dueño.getDocumentoIdentidad(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return -1;
    }
    
    @Override
    public boolean actualizar(Dueño dueño) {
        String sql = "UPDATE duenos SET nombre_completo = ?, direccion = ?, telefono = ?, email = ?, contacto_emergencia = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, dueño.getNombreCompleto());
            stmt.setString(2, dueño.getDireccion());
            stmt.setString(3, dueño.getTelefono());
            stmt.setString(4, dueño.getEmail());
            stmt.setString(5, dueño.getContactoEmergencia());
            stmt.setInt(6, dueño.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al actualizar dueno ID: " + dueño.getId(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE duenos SET activo = FALSE WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al eliminar dueno ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    public Optional<Dueño> encontrarPorDocumento(String documento) {
        String sql = "SELECT * FROM duenos WHERE documento_identidad = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, documento);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearDueño(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar dueno por documento: " + documento, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return Optional.empty();
    }
    
    private Dueño mapearDueño(ResultSet rs) throws SQLException {
        Dueño dueño = new Dueño();
        dueño.setId(rs.getInt("id"));
        dueño.setNombreCompleto(rs.getString("nombre_completo"));
        dueño.setDocumentoIdentidad(rs.getString("documento_identidad"));
        dueño.setDireccion(rs.getString("direccion"));
        dueño.setTelefono(rs.getString("telefono"));
        dueño.setEmail(rs.getString("email"));
        dueño.setContactoEmergencia(rs.getString("contacto_emergencia"));
        
        java.sql.Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            dueño.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }
        
        dueño.setActivo(rs.getBoolean("activo"));
        return dueño;
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