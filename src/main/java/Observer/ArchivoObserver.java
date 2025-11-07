package Observer;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class ArchivoObserver implements NotificacionObserver {
    @Override
    public void notificarStockMinimo(int producto_tipo_id, String nombre_producto, LocalDate fecha_vencimiento) {
        try {
            FileWriter writer = new FileWriter("stockminimo.log", true);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String registro = timestamp + " | TAREA_PENDIENTE | Producto_ID=" + producto_tipo_id + 
                            " | Nombre =" + nombre_producto + " | Reabastecer antes de =" + fecha_vencimiento + "\n";
            writer.write(registro);
            writer.close();
            System.out.println("Tarea registrada en stockminimo.log");
        } catch (Exception e) {
            LoggerManager.logError("Error al escribir en stoclminimo.log", e);
        }
    }
}
