package services;

import Dao.ConexionBD;
import Dao.MascotaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import model.Mascota.Mascota;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class HistorialPesoService {
    private Scanner scanner = new Scanner(System.in);
    private MascotaDAO mascotaDAO = new MascotaDAO();
    
    public void consultarHistorialPeso() {
        System.out.println("\n--- HISTORIAL DE PESO ---");
        
        System.out.print("ID de la mascota: ");
        int mascotaId = Integer.parseInt(scanner.nextLine());
        
        var mascotaOpt = mascotaDAO.encontrarPorId(mascotaId);
        
        if (mascotaOpt.isEmpty()) {
            System.out.println("Error: No se encontró ninguna mascota con el ID proporcionado");
            return;
        }
        
        Mascota mascota = mascotaOpt.get();
        
        // Consultar historial de peso desde la BD
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            String sql = "SELECT fecha_hora, peso_registrado " +
                        "FROM consultas_medicas " +
                        "WHERE mascota_id = ? AND peso_registrado IS NOT NULL " +
                        "ORDER BY fecha_hora ASC";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mascotaId);
            rs = stmt.executeQuery();
            
            System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("HISTORIAL DE PESO DEL PACIENTE");
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("\nDATOS DEL PACIENTE");
            System.out.println("Nombre: " + mascota.getNombre());
            System.out.println("Raza: " + obtenerNombreRaza(mascota.getRazaId()));
            
            System.out.println("\nREGISTRO DE PESOS");
            System.out.println("Fecha Consulta | Peso Registrado");
            System.out.println("-----------------------------------------");
            
            String ultimoPeso = "";
            boolean tieneRegistros = false;
            
            while (rs.next()) {
                tieneRegistros = true;
                String fecha = rs.getTimestamp("fecha_hora").toString();
                String peso = String.format("%.2f kg", rs.getDouble("peso_registrado"));
                System.out.println(fecha + " | " + peso);
                ultimoPeso = peso;
            }
            
            if (!tieneRegistros) {
                System.out.println("No hay registros de peso para esta mascota");
            } else {
                System.out.println("\n>> Último peso registrado: " + ultimoPeso);
            }
            
            LoggerManager.logInfo("Historial de peso consultado - Mascota ID: " + mascotaId);
            
        } catch (Exception e) {
            System.out.println("Error al consultar historial de peso: " + e.getMessage());
            LoggerManager.logError("Error en consultarHistorialPeso", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                LoggerManager.logError("Error cerrando recursos", e);
            }
        }
    }
    
    private String obtenerNombreRaza(int razaId) {
        // Implementar con RazaDAO si está disponible
        return "Raza ID: " + razaId;
    }
}