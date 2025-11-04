package services;
import Dao.CitaDAO;
import Dao.MascotaDAO;
import Dao.VeterinarioDAO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import model.Cita.Cita;
import model.Veterinario.Veterinario;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class CitaService {
    private CitaDAO citaDAO = new CitaDAO();
    private MascotaDAO mascotaDAO = new MascotaDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    /**
     * REGLA DE NEGOCIO CRÍTICA: Agendar cita sin solapamientos
     */
    public void agendarCita() {
        System.out.println("\n--- AGENDAR NUEVA CITA ---");
        
        // Validar mascota
        System.out.print("ID de la mascota: ");
        int mascotaId = Integer.parseInt(scanner.nextLine());
        
        var mascotaOpt = mascotaDAO.encontrarPorId(mascotaId);
        if (mascotaOpt.isEmpty()) {
            System.out.println("Error: No existe mascota con ID " + mascotaId);
            return;
        }
        
        System.out.println("Mascota: " + mascotaOpt.get().getNombre());
        
        // Validar veterinario
        System.out.println("\n--- VETERINARIOS ACTIVOS ---");
        List<Veterinario> veterinariosActivos = veterinarioDAO.encontrarTodos().stream()
            .filter(Veterinario::isActivo)
            .toList();

        if (veterinariosActivos.isEmpty()) {
            System.out.println("No hay veterinarios activos en este momento.");
            System.out.print("¿Desea continuar sin asignar veterinario? (si/no): ");
            String respuesta = scanner.nextLine();
            if (!respuesta.equalsIgnoreCase("si")) {
                return;
            }
        } else {
            System.out.println("Veterinarios disponibles:");
            for (Veterinario vet : veterinariosActivos) {
                System.out.printf("   ID: %d | %s | Especialidad: %s%n",
                    vet.getId(),
                    vet.getNombreCompleto(),
                    vet.getEspecialidad() != null ? vet.getEspecialidad() : "General");
            }
        }

        System.out.print("\nID del veterinario (opcional - Enter para omitir): ");
        String veterinarioIdStr = scanner.nextLine();
        Integer veterinarioId = null;

        if (!veterinarioIdStr.isEmpty()) {
            veterinarioId = Integer.parseInt(veterinarioIdStr);

            // Validar que el veterinario existe y está activo
            var veterinarioOpt = veterinarioDAO.encontrarPorId(veterinarioId);
            if (veterinarioOpt.isEmpty() || !veterinarioOpt.get().isActivo()) {
                System.out.println("Error: No existe veterinario activo con ID " + veterinarioId);
                return;
            }
            System.out.println("Veterinario asignado: " + veterinarioOpt.get().getNombreCompleto());
        }
        
        // Fecha y hora
        System.out.print("Fecha y hora (YYYY-MM-DD HH:MM): ");
        String fechaHoraStr = scanner.nextLine();
        LocalDateTime fechaHora;
        
        try {
            fechaHora = LocalDateTime.parse(fechaHoraStr, formatter);
        } catch (Exception e) {
            System.out.println("Error: Formato de fecha inválido. Use YYYY-MM-DD HH:MM");
            return;
        }
        
        // Validar que no sea en el pasado
        if (fechaHora.isBefore(LocalDateTime.now())) {
            System.out.println("Error: No se pueden agendar citas en el pasado");
            return;
        }
        
        // REGLA DE NEGOCIO: Verificar solapamiento si hay veterinario asignado
        if (veterinarioId != null) {
            if (citaDAO.existeSolapamiento(veterinarioId, fechaHora, 30)) {
                System.out.println("Error: El veterinario ya tiene una cita en ese horario");
                return;
            }
        }
        
        System.out.print("Motivo de la cita: ");
        String motivo = scanner.nextLine();
        
        System.out.print("Observaciones (opcional): ");
        String observaciones = scanner.nextLine();
        
        // Estado 1 = Programada (según datos iniciales de cita_estados)
        Cita nuevaCita = new Cita(mascotaId, fechaHora, motivo, 1);
        nuevaCita.setVeterinarioId(veterinarioId);
        nuevaCita.setObservaciones(observaciones);
        
        int id = citaDAO.guardar(nuevaCita);
        
        if (id > 0) {
            System.out.println("Cita agendada exitosamente con ID: " + id);
            LoggerManager.logInfo("Nueva cita agendada - Mascota ID: " + mascotaId + " - Fecha: " + fechaHora);
        } else {
            System.out.println("Error al agendar cita");
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Mostrar citas con información relacionada
     */
    public void listarCitas() {
        System.out.println("\n--- TODAS LAS CITAS ---");
        List<Cita> citas = citaDAO.encontrarTodos();
        
        if (citas.isEmpty()) {
            System.out.println("No hay citas agendadas.");
            return;
        }
        
        for (Cita cita : citas) {
            String estado = obtenerNombreEstado(cita.getEstadoId());
            String mascotaNombre = obtenerNombreMascota(cita.getMascotaId());
            String veterinarioNombre = cita.getVeterinarioId() != null ? 
                obtenerNombreVeterinario(cita.getVeterinarioId()) : "Sin asignar";
            
            System.out.printf("ID: %d | %s | Mascota: %s | Vet: %s | Estado: %s%n",
                cita.getId(),
                cita.getFechaHora().format(formatter),
                mascotaNombre,
                veterinarioNombre,
                estado);
        }
    }
    
    public void listarCitasPorMascota() {
        System.out.print("\nID de la mascota: ");
        int mascotaId = Integer.parseInt(scanner.nextLine());
        
        var mascotaOpt = mascotaDAO.encontrarPorId(mascotaId);
        if (mascotaOpt.isEmpty()) {
            System.out.println("No existe mascota con ID " + mascotaId);
            return;
        }
        
        System.out.println("Citas de: " + mascotaOpt.get().getNombre());
        List<Cita> citas = citaDAO.encontrarPorMascota(mascotaId);
        
        if (citas.isEmpty()) {
            System.out.println("No tiene citas agendadas.");
            return;
        }
        
        for (Cita cita : citas) {
            String estado = obtenerNombreEstado(cita.getEstadoId());
            String veterinarioNombre = cita.getVeterinarioId() != null ? 
                obtenerNombreVeterinario(cita.getVeterinarioId()) : "Sin asignar";
            
            System.out.printf(" %s | Vet: %s | Estado: %s | Motivo: %s%n",
                cita.getFechaHora().format(formatter),
                veterinarioNombre,
                estado,
                cita.getMotivo());
        }
    }
    
    public void listarCitasPorVeterinario() {
        System.out.print("\nID del veterinario: ");
        int veterinarioId = Integer.parseInt(scanner.nextLine());
        
        var veterinarioOpt = veterinarioDAO.encontrarPorId(veterinarioId);
        if (veterinarioOpt.isEmpty()) {
            System.out.println("No existe veterinario con ID " + veterinarioId);
            return;
        }
        
        Veterinario veterinario = veterinarioOpt.get();
        System.out.println("Citas del veterinario: " + veterinario.getNombreCompleto());
        
        List<Cita> citas = citaDAO.encontrarPorVeterinario(veterinarioId);
        
        if (citas.isEmpty()) {
            System.out.println("No tiene citas agendadas.");
            return;
        }
        
        for (Cita cita : citas) {
            String estado = obtenerNombreEstado(cita.getEstadoId());
            String mascotaNombre = obtenerNombreMascota(cita.getMascotaId());
            
            System.out.printf(" %s | Mascota: %s | Estado: %s | Motivo: %s%n",
                cita.getFechaHora().format(formatter),
                mascotaNombre,
                estado,
                cita.getMotivo());
        }
    }
    
    public void listarCitasPorEstado() {
        System.out.println("\nEstados disponibles:");
        System.out.println("1 - Programada");
        System.out.println("2 - Confirmada"); 
        System.out.println("3 - En Proceso");
        System.out.println("4 - Finalizada");
        System.out.println("5 - Cancelada");
        System.out.println("6 - No Asistió");
        
        System.out.print("Selecciona estado: ");
        int estadoId = Integer.parseInt(scanner.nextLine());
        
        List<Cita> citas = citaDAO.encontrarPorEstado(estadoId);
        
        if (citas.isEmpty()) {
            System.out.println("No hay citas con ese estado.");
            return;
        }
        
        System.out.println("Citas con estado: " + obtenerNombreEstado(estadoId));
        for (Cita cita : citas) {
            String mascotaNombre = obtenerNombreMascota(cita.getMascotaId());
            String veterinarioNombre = cita.getVeterinarioId() != null ? 
                obtenerNombreVeterinario(cita.getVeterinarioId()) : "Sin asignar";
            
            System.out.printf(" ID: %d | %s | Mascota: %s | Vet: %s | Motivo: %s%n",
                cita.getId(),
                cita.getFechaHora().format(formatter),
                mascotaNombre,
                veterinarioNombre,
                cita.getMotivo());
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Cambiar estado de cita
     */
    public void cambiarEstadoCita() {
        System.out.print("\nID de la cita: ");
        int citaId = Integer.parseInt(scanner.nextLine());
        
        var citaOpt = citaDAO.encontrarPorId(citaId);
        if (citaOpt.isEmpty()) {
            System.out.println("No existe cita con ID " + citaId);
            return;
        }
        
        Cita cita = citaOpt.get();
        System.out.println("Cita actual:");
        System.out.println("  Mascota: " + obtenerNombreMascota(cita.getMascotaId()));
        System.out.println("  Fecha: " + cita.getFechaHora().format(formatter));
        System.out.println("  Estado actual: " + obtenerNombreEstado(cita.getEstadoId()));
        
        System.out.println("\nNuevo estado:");
        System.out.println("1 - Programada");
        System.out.println("2 - Confirmada");
        System.out.println("3 - En Proceso");
        System.out.println("4 - Finalizada"); 
        System.out.println("5 - Cancelada");
        System.out.println("6 - No Asistió");
        
        System.out.print("Selecciona nuevo estado: ");
        int nuevoEstado = Integer.parseInt(scanner.nextLine());
        
        if (citaDAO.actualizarEstado(citaId, nuevoEstado)) {
            System.out.println("Estado actualizado exitosamente");
            LoggerManager.logInfo("Estado de cita cambiado - Cita ID: " + citaId + " - Nuevo estado: " + nuevoEstado);
        } else {
            System.out.println("Error al actualizar estado");
        }
    }
    
    /**
    * REGLA DE NEGOCIO: Ver veterinarios disponibles en una fecha específica
    */
   public void verDisponibilidadVeterinarios() {
       System.out.println("\n--- VER DISPONIBILIDAD DE VETERINARIOS ---");

       System.out.print("Fecha para ver disponibilidad (YYYY-MM-DD): ");
       String fechaStr = scanner.nextLine();

       LocalDateTime fechaConsulta;
       try {
           fechaConsulta = LocalDate.parse(fechaStr).atTime(8, 0); // Empieza a las 8:00 AM
       } catch (Exception e) {
           System.out.println("Error: Formato de fecha inválido. Use YYYY-MM-DD");
           return;
       }

       System.out.println("\nDisponibilidad para: " + fechaStr);
       System.out.println("======================================");

       List<Veterinario> veterinariosActivos = veterinarioDAO.encontrarTodos().stream()
           .filter(Veterinario::isActivo)
           .toList();

       if (veterinariosActivos.isEmpty()) {
           System.out.println("No hay veterinarios activos.");
           return;
       }

       for (Veterinario vet : veterinariosActivos) {
           System.out.printf("\n️ %s (%s):%n",
               vet.getNombreCompleto(),
               vet.getEspecialidad() != null ? vet.getEspecialidad() : "General");

           // Verificar citas para este veterinario en la fecha consultada
           boolean tieneCitas = false;

           // Consultar citas del día para este veterinario
           List<Cita> citasDelDia = citaDAO.encontrarPorVeterinario(vet.getId()).stream()
               .filter(cita -> cita.getFechaHora().toLocalDate().equals(fechaConsulta.toLocalDate()))
               .filter(cita -> cita.getEstadoId() == 1 || cita.getEstadoId() == 2) // Solo programadas y confirmadas
               .toList();

           if (citasDelDia.isEmpty()) {
               System.out.println("Disponible todo el día");
           } else {
               tieneCitas = true;
               System.out.println("Citas agendadas:");
               for (Cita cita : citasDelDia) {
                   String mascotaNombre = obtenerNombreMascota(cita.getMascotaId());
                   System.out.printf("%s - %s | %s%n",
                       cita.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")),
                       mascotaNombre,
                       obtenerNombreEstado(cita.getEstadoId()));
               }
           }

           // Mostrar horarios libres sugeridos
           if (!tieneCitas) {
               System.out.println("Horarios sugeridos: 09:00, 11:00, 15:00, 17:00");
           }
       }
   }
    
    /**
     * REGLA DE NEGOCIO: Mostrar lista de veterinarios disponibles
     */
    public void listarVeterinarios() {
        System.out.println("\n--- VETERINARIOS DISPONIBLES ---");
        List<Veterinario> veterinarios = veterinarioDAO.encontrarTodos();

        if (veterinarios.isEmpty()) {
            System.out.println("No hay veterinarios registrados.");
            return;
        }

        // Separar activos e inactivos
        List<Veterinario> veterinariosActivos = veterinarios.stream()
            .filter(Veterinario::isActivo)
            .toList();

        List<Veterinario> veterinariosInactivos = veterinarios.stream()
            .filter(v -> !v.isActivo())
            .toList();

        System.out.println("\nVETERINARIOS ACTIVOS:");
        if (veterinariosActivos.isEmpty()) {
            System.out.println("No hay veterinarios activos en este momento.");
        } else {
            for (Veterinario veterinario : veterinariosActivos) {
                System.out.printf("ID: %d | %s | Especialidad: %s | Tel: %s%n",
                    veterinario.getId(),
                    veterinario.getNombreCompleto(),
                    veterinario.getEspecialidad() != null ? veterinario.getEspecialidad() : "General",
                    veterinario.getTelefono() != null ? veterinario.getTelefono() : "No registrado");
            }
        }

        if (!veterinariosInactivos.isEmpty()) {
            System.out.println("\nVETERINARIOS INACTIVOS:");
            for (Veterinario veterinario : veterinariosInactivos) {
                System.out.printf("ID: %d | %s | Especialidad: %s%n",
                    veterinario.getId(),
                    veterinario.getNombreCompleto(),
                    veterinario.getEspecialidad() != null ? veterinario.getEspecialidad() : "General");
            }
        }

        System.out.println("\nSolo los veterinarios ACTIVOS pueden ser asignados a citas nuevas");
    }
    
    // Métodos auxiliares para obtener nombres
    private String obtenerNombreEstado(int estadoId) {
        return switch (estadoId) {
            case 1 -> "Programada";
            case 2 -> "Confirmada";
            case 3 -> "En Proceso";
            case 4 -> "Finalizada";
            case 5 -> "Cancelada";
            case 6 -> "No Asistió";
            default -> "Desconocido";
        };
    }
    
    private String obtenerNombreMascota(int mascotaId) {
        var mascotaOpt = mascotaDAO.encontrarPorId(mascotaId);
        return mascotaOpt.map(mascota -> mascota.getNombre()).orElse("Desconocida");
    }
    
    private String obtenerNombreVeterinario(int veterinarioId) {
        var vetOpt = veterinarioDAO.encontrarPorId(veterinarioId);
        return vetOpt.map(vet -> vet.getNombreCompleto()).orElse("Desconocido");
    }
}