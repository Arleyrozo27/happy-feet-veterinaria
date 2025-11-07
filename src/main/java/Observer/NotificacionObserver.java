package Observer;

import java.time.LocalDate;

/**
 *
 * @author arley
 */

public interface NotificacionObserver {
    void notificarProximoControl(int mascotaId, int dueñoId, LocalDate fechaControl);
}