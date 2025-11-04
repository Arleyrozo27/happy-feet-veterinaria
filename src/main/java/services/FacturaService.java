/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import Dao.DueñoDAO;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import model.Dueno.Dueño;
import model.Factura.Factura;
import model.ItemFactura.ItemFactura;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class FacturaService {
    private DueñoDAO dueñoDAO = new DueñoDAO();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * REGLA DE NEGOCIO: Generar factura rápida en texto plano
     */
    public void generarFacturaRapida() {
        System.out.println("\n--- GENERAR FACTURA RÁPIDA ---");
        
        // Validar dueño
        System.out.print("ID del dueño: ");
        int dueñoId = Integer.parseInt(scanner.nextLine());
        
        var dueñoOpt = dueñoDAO.encontrarPorId(dueñoId);
        if (dueñoOpt.isEmpty()) {
            System.out.println("❌ Error: No existe dueño con ID " + dueñoId);
            return;
        }
        
        var dueño = dueñoOpt.get();
        
        // Crear factura
        String numeroFactura = "FACT-" + System.currentTimeMillis();
        Factura factura = new Factura(dueñoId, numeroFactura);
        
        // Agregar items manualmente (por simplicidad)
        System.out.println("\nAgregar servicios/productos a la factura:");
        
        boolean agregarMas = true;
        while (agregarMas) {
            System.out.print("Descripción del item: ");
            String descripcion = scanner.nextLine();
            
            System.out.print("Cantidad: ");
            int cantidad = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Precio unitario: ");
            double precio = Double.parseDouble(scanner.nextLine());
            
            ItemFactura item = new ItemFactura(descripcion, cantidad, precio);
            factura.agregarItem(item);
            
            System.out.print("¿Agregar otro item? (si/no): ");
            agregarMas = scanner.nextLine().equalsIgnoreCase("si");
        }
        
        // Calcular totales
        factura.calcularTotales();
        
        // Método de pago
        System.out.print("Método de pago (Efectivo/Tarjeta/Transferencia): ");
        factura.setMetodoPago(scanner.nextLine());
        
        // Generar y mostrar factura
        System.out.println("\n" + generarTextoFactura(factura, dueño));
        
        System.out.print("¿Confirmar factura? (si/no): ");
        if (scanner.nextLine().equalsIgnoreCase("si")) {
            System.out.println("Factura generada exitosamente: " + factura.getNumeroFactura());
            LoggerManager.logInfo("Factura generada - Número: " + factura.getNumeroFactura() + " - Total: " + factura.getTotal());
        } else {
            System.out.println("Factura cancelada");
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Generar factura en formato texto plano
     */
    private String generarTextoFactura(Factura factura, Dueño dueño) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("============================================\n");
        sb.append("           HAPPY FEET VETERINARIA          \n");
        sb.append("============================================\n");
        sb.append("Factura: ").append(factura.getNumeroFactura()).append("\n");
        sb.append("Fecha: ").append(factura.getFechaEmision().format(formatter)).append("\n");
        sb.append("--------------------------------------------\n");
        sb.append("CLIENTE:\n");
        sb.append("  ").append(dueño.getNombreCompleto()).append("\n");
        sb.append("  Doc: ").append(dueño.getDocumentoIdentidad()).append("\n");
        sb.append("  Tel: ").append(dueño.getTelefono()).append("\n");
        sb.append("--------------------------------------------\n");
        sb.append("ITEMS:\n");
        
        for (ItemFactura item : factura.getItems()) {
            sb.append(String.format("  %-20s %2d x $%-8.2f $%-8.2f%n",
                item.getDescripcion(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()));
        }
        
        sb.append("--------------------------------------------\n");
        sb.append(String.format("SUBTOTAL:                     $%-10.2f%n", factura.getSubtotal()));
        sb.append(String.format("IMPUESTO (19%%):               $%-10.2f%n", factura.getImpuesto()));
        sb.append(String.format("DESCUENTO:                    $%-10.2f%n", factura.getDescuento()));
        sb.append("--------------------------------------------\n");
        sb.append(String.format("TOTAL:                        $%-10.2f%n", factura.getTotal()));
        sb.append("--------------------------------------------\n");
        sb.append("Método de pago: ").append(factura.getMetodoPago()).append("\n");
        sb.append("Estado: ").append(factura.getEstado()).append("\n");
        sb.append("============================================\n");
        sb.append("     ¡Gracias por confiar en nosotros!     \n");
        sb.append("============================================\n");
        
        return sb.toString();
    }
}