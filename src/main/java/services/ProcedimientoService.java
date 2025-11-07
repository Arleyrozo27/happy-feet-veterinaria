package services;

import Dao.ConexionBD;
import Observer.NotificacionObserver;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class ProcedimientoService {
    private List<NotificacionObserver> observadores = new ArrayList<>();
    
    public void registrarObservador(NotificacionObserver observador) {
        observadores.add(observador);
        System.out.println("Observador registrado: " + observador.getClass().getSimpleName());
    }
    
    public void removerObservador(NotificacionObserver observador) {
        observadores.remove(observador);
    }
    
    public void actualizarEstadoProcedimiento(int procedimientoId, String nuevoEstado) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            
            // Consultar el procedimiento
            String sqlSelect = "SELECT * FROM procedimientos_especiales WHERE id = ?";
            stmt = conn.prepareStatement(sqlSelect);
            stmt.setInt(1, procedimientoId);
            var rs = stmt.executeQuery();
            
            if (rs.next()) {
                String estadoActual = rs.getString("estado");
                LocalDate proximoControl = rs.getDate("proximo_control") != null ? 
                    rs.getDate("proximo_control").toLocalDate() : null;
                int mascotaId = rs.getInt("mascota_id");
                int dueñoId = obtenerDueñoId(mascotaId); // Método auxiliar
                
                // Actualizar estado
                String sqlUpdate = "UPDATE procedimientos_especiales SET estado = ? WHERE id = ?";
                stmt = conn.prepareStatement(sqlUpdate);
                stmt.setString(1, nuevoEstado);
                stmt.setInt(2, procedimientoId);
                stmt.executeUpdate();
                
                System.out.println("Procedimiento ID " + procedimientoId + " actualizado a: " + nuevoEstado);
                
                // Verificar si debe notificar
                if ("Finalizado".equals(nuevoEstado) && proximoControl != null) {
                    notificarObservadores(mascotaId, dueñoId, proximoControl);
                }
            } else {
                System.out.println("No se encontró procedimiento con ID: " + procedimientoId);
            }
            
        } catch (Exception e) {
            System.out.println("Error al actualizar procedimiento: " + e.getMessage());
            LoggerManager.logError("Error en actualizarEstadoProcedimiento", e);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                LoggerManager.logError("Error cerrando recursos", e);
            }
        }
    }
    
    private int obtenerDueñoId(int mascotaId) {
        // Método auxiliar para obtener dueño_id de una mascota
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            String sql = "SELECT dueno_id FROM mascotas WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mascotaId);
            var rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("dueno_id");
            }
        } catch (Exception e) {
            LoggerManager.logError("Error obteniendo dueño_id", e);
        }
        
        return 1; // Valor por defecto si hay error
    }
    
    private void notificarObservadores(int mascotaId, int dueñoId, LocalDate fechaControl) {
        System.out.println("\n🔔 Notificando observadores sobre control próximo...");
        for (NotificacionObserver observador : observadores) {
            observador.notificarProximoControl(mascotaId, dueñoId, fechaControl);
        }
        LoggerManager.logInfo("Notificaciones enviadas para control próximo - Mascota ID: " + mascotaId);
    }
    
    // Método para probar manualmente
    public void probarNotificacionManual() {
        System.out.println("\n--- PRUEBA MANUAL DE NOTIFICACIÓN ---");
        notificarObservadores(1, 1, LocalDate.now().plusDays(7));
    }
}