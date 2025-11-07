package Observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 *
 * @author arley
 */

public interface NotificacionObserver {
    void notificarStockMinimo(int producto_tipo_id,String nombre_producto, LocalDate fecha_vencimiento);
    String sql = "INSERT INTO inventario (nombre_producto, producto_tipo_id, fecha_vencimiento,) VALUES (?, ?, ?)";
    Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
}