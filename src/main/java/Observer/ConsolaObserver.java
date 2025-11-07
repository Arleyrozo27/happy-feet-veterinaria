package Observer;

import java.time.LocalDate;

/**
 *
 * @author arley
 */

public class ConsolaObserver implements NotificacionObserver {
    @Override
    public void notificarStockMinimo(int producto_tipo_id,String nombre_producto, LocalDate fecha_vencimiento) {
        System.out.println("\n*** ALERTA DE STOCK BAJO ***");
        System.out.println("Producto ID: " + producto_tipo_id);
        System.out.println("Nombre: " + nombre_producto);
        System.out.println("Fecha antes de vencer: " + fecha_vencimiento);
        System.out.println("Tarea: Contactar para obtener mas producto.");
        System.out.println("****************************************");
    }
}
