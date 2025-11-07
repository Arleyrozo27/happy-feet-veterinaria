package Observer;

import java.time.LocalDate;

/**
 *
 * @author arley
 */

public class ConsolaObserver implements NotificacionObserver {
    @Override
    public void notificarProximoControl(int mascotaId, int dueñoId, LocalDate fechaControl) {
        System.out.println("\n*** RECORDATORIO DE CONTROL PRÓXIMO ***");
        System.out.println("Mascota ID: " + mascotaId);
        System.out.println("Dueño ID: " + dueñoId);
        System.out.println("Fecha Sugerida de Control: " + fechaControl);
        System.out.println("Tarea: Contactar al dueño para agendar.");
        System.out.println("****************************************");
    }
}
