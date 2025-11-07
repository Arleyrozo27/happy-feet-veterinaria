package services;

import Dao.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Scanner;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class ReporteProductosService {
    private Scanner scanner = new Scanner(System.in);
    
    public void generarReporteProductosVendidos() {
        System.out.println("\n--- REPORTE DE PRODUCTOS MÁS VENDIDOS ---");
        
        System.out.print("Fecha de inicio (YYYY-MM-DD): ");
        String fechaInicioStr = scanner.nextLine();
        
        System.out.print("Fecha de fin (YYYY-MM-DD): ");
        String fechaFinStr = scanner.nextLine();
        
        LocalDate fechaInicio, fechaFin;
        try {
            fechaInicio = LocalDate.parse(fechaInicioStr);
            fechaFin = LocalDate.parse(fechaFinStr);
        } catch (Exception e) {
            System.out.println("Error: Formato de fecha inválido");
            return;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            
            // Consulta SQL para productos más vendidos
            String sql = "SELECT p.nombre_producto, " +
                        "SUM(if.cantidad) as cantidad_vendida, " +
                        "SUM(if.subtotal) as ingresos_totales " +
                        "FROM items_factura if " +
                        "JOIN inventario p ON if.producto_id = p.id " +
                        "JOIN facturas f ON if.factura_id = f.id " +
                        "WHERE f.fecha_emision BETWEEN ? AND ? " +
                        "AND if.tipo_item = 'Producto' " +
                        "GROUP BY p.nombre_producto " +
                        "ORDER BY ingresos_totales DESC";
            
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, java.sql.Date.valueOf(fechaInicio));
            stmt.setDate(2, java.sql.Date.valueOf(fechaFin));
            rs = stmt.executeQuery();
            
            // Mostrar reporte
            System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("REPORTE DE PRODUCTOS MÁS VENDIDOS");
            System.out.println("Período: " + fechaInicio + " - " + fechaFin);
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            
            System.out.printf("%-35s | %-15s | %-15s%n", 
                "Producto", "Cantidad Vendida", "Ingresos Totales");
            System.out.println("---------------------------------------------------------------------");
            
            boolean tieneDatos = false;
            double ingresosTotalesPeriodo = 0;
            
            while (rs.next()) {
                tieneDatos = true;
                String producto = rs.getString("nombre_producto");
                int cantidad = rs.getInt("cantidad_vendida");
                double ingresos = rs.getDouble("ingresos_totales");
                ingresosTotalesPeriodo += ingresos;
                
                System.out.printf("%-35s | %-15d | %-15s%n", 
                    producto, cantidad, formatearMoneda(ingresos));
            }
            
            if (!tieneDatos) {
                System.out.println("No hay ventas de productos en el período seleccionado");
            } else {
                System.out.println("---------------------------------------------------------------------");
                System.out.printf("%-35s | %-15s | %-15s%n", 
                    "TOTAL PERÍODO", "", formatearMoneda(ingresosTotalesPeriodo));
            }
            
            LoggerManager.logInfo("Reporte de productos generado - Período: " + fechaInicio + " a " + fechaFin);
            
        } catch (Exception e) {
            System.out.println("Error al generar reporte: " + e.getMessage());
            LoggerManager.logError("Error en generarReporteProductosVendidos", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                LoggerManager.logError("Error cerrando recursos", e);
            }
        }
    }
    
    private String formatearMoneda(double cantidad) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return formatter.format(cantidad);
    }
}