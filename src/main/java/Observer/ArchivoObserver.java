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
    public void notificarProximoControl(int mascotaId, int dueñoId, LocalDate fechaControl) {
        try {
            FileWriter writer = new FileWriter("agenda_controles.log", true);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String registro = timestamp + " | TAREA_PENDIENTE | Mascota_ID=" + mascotaId + 
                            " | Dueño_ID=" + dueñoId + " | Fecha_Control_Sugerida=" + fechaControl + "\n";
            writer.write(registro);
            writer.close();
            System.out.println("✓ Tarea registrada en agenda_controles.log");
        } catch (Exception e) {
            LoggerManager.logError("Error al escribir en agenda_controles.log", e);
        }
    }
}
