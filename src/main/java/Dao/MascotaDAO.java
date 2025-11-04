package Dao;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Mascota.Mascota;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class MascotaDAO implements BaseDAO<Mascota> {
    
    @Override
    public Optional<Mascota> encontrarPorId(int id) {
        String sql = "SELECT * FROM mascotas WHERE id = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearMascota(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar mascota por ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Mascota> encontrarTodos() {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = "SELECT * FROM mascotas WHERE activo = TRUE ORDER BY id";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                mascotas.add(mapearMascota(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener todas las mascotas", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return mascotas;
    }
    
    public List<Mascota> encontrarPorDueño(int dueñoId) { // Cambiado
        List<Mascota> mascotas = new ArrayList<>();
        String sql = "SELECT * FROM mascotas WHERE dueno_id = ? AND activo = TRUE ORDER BY id";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, dueñoId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                mascotas.add(mapearMascota(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar mascotas del dueño ID: " + dueñoId, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return mascotas;
    }
    
    @Override
    public int guardar(Mascota mascota) {
        String sql = "INSERT INTO mascotas (dueno_id, nombre, raza_id, fecha_nacimiento, sexo, peso_actual, microchip, tatuaje, url_foto, alergias, condiciones_preexistentes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, mascota.getDueñoId()); // Cambiado
            stmt.setString(2, mascota.getNombre());
            stmt.setInt(3, mascota.getRazaId());
            stmt.setDate(4, mascota.getFechaNacimiento() != null ? 
                Date.valueOf(mascota.getFechaNacimiento()) : null);
            stmt.setString(5, mascota.getSexo());
            stmt.setDouble(6, mascota.getPesoActual());
            stmt.setString(7, mascota.getMicrochip());
            stmt.setString(8, mascota.getTatuaje());
            stmt.setString(9, mascota.getUrlFoto());
            stmt.setString(10, mascota.getAlergias());
            stmt.setString(11, mascota.getCondicionesPreexistentes());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al guardar mascota: " + mascota.getNombre(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return -1;
    }
    
    @Override
    public boolean actualizar(Mascota mascota) {
        String sql = "UPDATE mascotas SET nombre = ?, raza_id = ?, fecha_nacimiento = ?, sexo = ?, peso_actual = ?, microchip = ?, tatuaje = ?, url_foto = ?, alergias = ?, condiciones_preexistentes = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, mascota.getNombre());
            stmt.setInt(2, mascota.getRazaId());
            stmt.setDate(3, mascota.getFechaNacimiento() != null ? 
                Date.valueOf(mascota.getFechaNacimiento()) : null);
            stmt.setString(4, mascota.getSexo());
            stmt.setDouble(5, mascota.getPesoActual());
            stmt.setString(6, mascota.getMicrochip());
            stmt.setString(7, mascota.getTatuaje());
            stmt.setString(8, mascota.getUrlFoto());
            stmt.setString(9, mascota.getAlergias());
            stmt.setString(10, mascota.getCondicionesPreexistentes());
            stmt.setInt(11, mascota.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al actualizar mascota ID: " + mascota.getId(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE mascotas SET activo = FALSE WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al eliminar mascota ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    private Mascota mapearMascota(ResultSet rs) throws SQLException {
        Mascota mascota = new Mascota();
        mascota.setId(rs.getInt("id"));
        mascota.setDueñoId(rs.getInt("dueno_id"));
        mascota.setNombre(rs.getString("nombre"));
        mascota.setRazaId(rs.getInt("raza_id"));

        // Fecha nacimiento (DATE)
        java.sql.Date fechaNacimiento = rs.getDate("fecha_nacimiento");
        if (fechaNacimiento != null) {
            mascota.setFechaNacimiento(fechaNacimiento.toLocalDate());
        }

        mascota.setSexo(rs.getString("sexo"));
        mascota.setPesoActual(rs.getDouble("peso_actual"));
        mascota.setMicrochip(rs.getString("microchip"));
        mascota.setTatuaje(rs.getString("tatuaje"));
        mascota.setUrlFoto(rs.getString("url_foto"));
        mascota.setAlergias(rs.getString("alergias"));
        mascota.setCondicionesPreexistentes(rs.getString("condiciones_preexistentes"));

        // Fecha registro (DATETIME)
        java.sql.Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            mascota.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        mascota.setActivo(rs.getBoolean("activo"));
        return mascota;
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
