package services;

import Dao.ConexionBD;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class ContratoAdopcionService {
    private Scanner scanner = new Scanner(System.in);
    
    public void generarContratoAdopcion() {
        System.out.println("\n--- GENERAR CONTRATO DE ADOPCIÓN ---");
        
        System.out.print("ID de la adopción: ");
        int adopcionId = Integer.parseInt(scanner.nextLine());
        
        // Consultar datos de la adopción desde BD
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            
            // Verificar si la adopción existe y no tiene contrato
            String sqlCheck = "SELECT a.*, d.nombre_completo, d.documento_identidad, " +
                             "m.nombre as nombre_mascota, m.raza_id, m.microchip " +
                             "FROM adopciones a " +
                             "JOIN duenos d ON a.dueño_id = d.id " +
                             "JOIN mascotas m ON a.mascota_id = m.id " +
                             "WHERE a.id = ? AND a.contrato_texto IS NULL";
            
            stmt = conn.prepareStatement(sqlCheck);
            stmt.setInt(1, adopcionId);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("Error: ID de adopción no válido o ya tiene contrato generado");
                return;
            }
            
            // Obtener datos
            String nombreDueño = rs.getString("nombre_completo");
            String documentoDueño = rs.getString("documento_identidad");
            String nombreMascota = rs.getString("nombre_mascota");
            String microchip = rs.getString("microchip");
            
            // Generar contrato
            String contratoTexto = generarTextoContrato(nombreDueño, documentoDueño, 
                nombreMascota, microchip);
            
            // Guardar archivo
            String nombreArchivo = "contrato_adopcion_" + adopcionId + ".txt";
            FileWriter writer = new FileWriter(nombreArchivo);
            writer.write(contratoTexto);
            writer.close();
            
            // Actualizar BD con el contrato
            String sqlUpdate = "UPDATE adopciones SET contrato_texto = ? WHERE id = ?";
            stmt = conn.prepareStatement(sqlUpdate);
            stmt.setString(1, contratoTexto);
            stmt.setInt(2, adopcionId);
            stmt.executeUpdate();
            
            System.out.println("Contrato para la adopción ID " + adopcionId + 
                " generado y guardado exitosamente en '" + nombreArchivo + 
                "' y actualizado en la base de datos.");
                
            LoggerManager.logInfo("Contrato de adopción generado - ID: " + adopcionId);
            
        } catch (Exception e) {
            System.out.println("Error al generar contrato: " + e.getMessage());
            LoggerManager.logError("Error en generarContratoAdopcion", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                LoggerManager.logError("Error cerrando recursos", e);
            }
        }
    }
    
    private String generarTextoContrato(String nombreDueño, String documentoDueño,
                                      String nombreMascota, String microchip) {
        
        DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaActual = LocalDateTime.now().format(fechaFormatter);
        
        return "--- CONTRATO DE ADOPCIÓN \"HAPPY FEET\" ---\n\n" +
               "FECHA: " + fechaActual + "\n\n" +
               "DATOS DEL ADOPTANTE:\n" +
               "Nombre: " + nombreDueño + "\n" +
               "Documento: " + documentoDueño + "\n\n" +
               "DATOS DE LA MASCOTA:\n" +
               "Nombre: " + nombreMascota + "\n" +
               "Identificación: " + (microchip != null ? microchip : "N/A") + "\n\n" +
               "CLÁUSULAS Y COMPROMISOS:\n" +
               "1. El adoptante se compromete a proporcionar todos los cuidados necesarios.\n" +
               "2. Garantizará alimentación adecuada y atención veterinaria regular.\n" +
               "3. Proporcionará un hogar amoroso y seguro para la mascota.\n" +
               "4. Notificará cualquier cambio de domicilio a la clínica.\n\n" +
               "FIRMA DEL ADOPTANTE: _________________________\n" +
               "FIRMA DEL REPRESENTANTE: _____________________\n" +
               "FECHA: _________________________";
    }
}