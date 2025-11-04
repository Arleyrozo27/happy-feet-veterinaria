package services;

/**
 *
 * @author arley
 */

import Dao.ProductoDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import model.Producto.Producto;
import util.LoggerManager;

public class ProductoService {
    private ProductoDAO productoDAO = new ProductoDAO();
    private Scanner scanner = new Scanner(System.in);
    
    /**
     * REGLA DE NEGOCIO: Validar que no exista producto con mismo nombre
     */
    public void registrarProducto() {
        System.out.println("\n--- REGISTRAR NUEVO PRODUCTO ---");
        
        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Tipo de producto (1=Medicamento, 2=Vacuna, 3=Insumo Médico, 4=Alimento, 5=Accesorio): ");
        int tipoId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();
        
        System.out.print("Fabricante: ");
        String fabricante = scanner.nextLine();
        
        System.out.print("Precio de venta: ");
        double precioVenta = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Precio de compra (opcional): ");
        String precioCompraStr = scanner.nextLine();
        
        System.out.print("Cantidad stock inicial: ");
        int stock = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Stock mínimo para alerta: ");
        int stockMinimo = Integer.parseInt(scanner.nextLine());
        
        System.out.print("¿Requiere receta? (si/no): ");
        boolean requiereReceta = scanner.nextLine().equalsIgnoreCase("si");
        
        System.out.print("Fecha vencimiento (YYYY-MM-DD) o Enter para omitir: ");
        String fechaVencStr = scanner.nextLine();
        
        System.out.print("Lote (opcional): ");
        String lote = scanner.nextLine();
        
        System.out.print("Unidad de medida (unidad, caja, ml, etc.): ");
        String unidadMedida = scanner.nextLine();
        
        Producto nuevoProducto = new Producto(nombre, tipoId, precioVenta);
        nuevoProducto.setDescripcion(descripcion);
        nuevoProducto.setFabricante(fabricante);
        nuevoProducto.setCantidadStock(stock);
        nuevoProducto.setStockMinimo(stockMinimo);
        nuevoProducto.setRequiereReceta(requiereReceta);
        nuevoProducto.setLote(lote);
        nuevoProducto.setUnidadMedida(unidadMedida);
        
        if (!precioCompraStr.isEmpty()) {
            nuevoProducto.setPrecioCompra(Double.parseDouble(precioCompraStr));
        }
        
        if (!fechaVencStr.isEmpty()) {
            nuevoProducto.setFechaVencimiento(LocalDate.parse(fechaVencStr));
        }
        
        int id = productoDAO.guardar(nuevoProducto);
        
        if (id > 0) {
            System.out.println("Producto registrado exitosamente con ID: " + id);
            LoggerManager.logInfo("Nuevo producto registrado: " + nombre + " - ID: " + id);
        } else {
            System.out.println("Error al registrar producto");
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Mostrar alertas de stock bajo
     */
    public void listarProductos() {
        System.out.println("\n--- INVENTARIO COMPLETO ---");
        List<Producto> productos = productoDAO.encontrarTodos();
        
        if (productos.isEmpty()) {
            System.out.println("No hay productos en inventario.");
            return;
        }
        
        for (Producto producto : productos) {
            String alerta = "";
            if (producto.necesitaReabastecimiento()) {
                alerta = "STOCK BAJO";
            } else if (producto.estaProximoAVencer()) {
                alerta = "PRÓXIMO A VENCER";
            }
            
            System.out.printf("ID: %d | %s | Stock: %d/%d | Precio: $%.2f%s%n",
                producto.getId(),
                producto.getNombreProducto(),
                producto.getCantidadStock(),
                producto.getStockMinimo(),
                producto.getPrecioVenta(),
                alerta);
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Alertas automáticas de stock bajo
     */
    public void mostrarAlertasStockBajo() {
        System.out.println("\n--- ALERTAS: STOCK BAJO ---");
        List<Producto> productosBajos = productoDAO.encontrarConStockBajo();
        
        if (productosBajos.isEmpty()) {
            System.out.println("No hay productos con stock bajo");
            return;
        }
        
        System.out.println("Productos que necesitan reabastecimiento:");
        for (Producto producto : productosBajos) {
            System.out.printf(" %s - Stock: %d (Mínimo: %d)%n",
                producto.getNombreProducto(),
                producto.getCantidadStock(),
                producto.getStockMinimo());
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Alertas de productos próximos a vencer
     */
    public void mostrarProductosProximosAVencer() {
        System.out.println("\n--- ALERTAS: PRÓXIMOS A VENCER ---");
        List<Producto> productosProximos = productoDAO.encontrarProximosAVencer();
        
        if (productosProximos.isEmpty()) {
            System.out.println("No hay productos próximos a vencer");
            return;
        }
        
        System.out.println("Productos que vencen en los próximos 30 días:");
        for (Producto producto : productosProximos) {
            System.out.printf(" %s - Vence: %s - Lote: %s%n",
                producto.getNombreProducto(),
                producto.getFechaVencimiento(),
                producto.getLote() != null ? producto.getLote() : "N/A");
        }
    }
    
    /**
     * REGLA DE NEGOCIO CRÍTICA: Descontar stock con validación
     */
    public void descontarStock() {
        System.out.println("\n--- DESCONTAR STOCK ---");
        
        System.out.print("ID del producto: ");
        int productoId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Cantidad a descontar: ");
        int cantidad = Integer.parseInt(scanner.nextLine());
        
        // Validar que el producto existe y tiene stock suficiente
        var productoOpt = productoDAO.encontrarPorId(productoId);
        if (productoOpt.isEmpty()) {
            System.out.println("Error: No existe producto con ID " + productoId);
            return;
        }
        
        Producto producto = productoOpt.get();
        if (producto.getCantidadStock() < cantidad) {
            System.out.println("Error: Stock insuficiente. Stock actual: " + producto.getCantidadStock());
            return;
        }
        
        if (productoDAO.descontarStock(productoId, cantidad)) {
            System.out.println("Stock descontado exitosamente");
            System.out.println("- Nuevo stock: " + (producto.getCantidadStock() - cantidad));
            LoggerManager.logInfo("Stock descontado - Producto: " + producto.getNombreProducto() + " - Cantidad: " + cantidad);
        } else {
            System.out.println("Error al descontar stock");
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Buscar producto por nombre
     */
    public void buscarProducto() {
        System.out.print("\nIngrese nombre o parte del nombre del producto: ");
        String busqueda = scanner.nextLine().toLowerCase();
        
        List<Producto> productos = productoDAO.encontrarTodos();
        List<Producto> resultados = productos.stream()
            .filter(p -> p.getNombreProducto().toLowerCase().contains(busqueda))
            .toList();
        
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron productos con: " + busqueda);
            return;
        }
        
        System.out.println("Productos encontrados (" + resultados.size() + "):");
        for (Producto producto : resultados) {
            System.out.printf("   ID: %d | %s | Stock: %d | Precio: $%.2f%n",
                producto.getId(),
                producto.getNombreProducto(),
                producto.getCantidadStock(),
                producto.getPrecioVenta());
        }
    }
}