package services;

import Dao.CitaDAO;
import Dao.VeterinarioDAO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import model.Veterinario.Veterinario;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class DisponibilidadVeterinarioService {
    private Scanner scanner = new Scanner(System.in);
    private CitaDAO citaDAO = new CitaDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    
    public void verificarDisponibilidad() {
        System.out.println("\n--- VERIFICAR DISPONIBILIDAD VETERINARIO ---");
        
        System.out.print("ID del Veterinario: ");
        int veterinarioId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Fecha de la cita (YYYY-MM-DD): ");
        String fechaStr = scanner.nextLine();
        
        System.out.print("Hora de la cita (HH:MM): ");
        String horaStr = scanner.nextLine();
        
        // Validar veterinario
        var vetOpt = veterinarioDAO.encontrarPorId(veterinarioId);
        if (vetOpt.isEmpty()) {
            System.out.println("Error: No existe veterinario con ID " + veterinarioId);
            return;
        }
        
        Veterinario veterinario = vetOpt.get();
        
        // Validar formato de fecha y hora
        LocalDateTime fechaHoraSolicitada;
        try {
            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime hora = LocalTime.parse(horaStr);
            fechaHoraSolicitada = LocalDateTime.of(fecha, hora);
        } catch (Exception e) {
            System.out.println("Error: Formato de fecha u hora inválido");
            return;
        }
        
        // Validar horario laboral (9:00 - 17:00)
        LocalTime horaInicio = fechaHoraSolicitada.toLocalTime();
        if (horaInicio.isBefore(LocalTime.of(9, 0)) || horaInicio.isAfter(LocalTime.of(17, 0))) {
            System.out.println("Error: Horario fuera del horario laboral (9:00 - 17:00)");
            return;
        }
        
        // Verificar solapamiento
        LocalDateTime inicioNuevo = fechaHoraSolicitada;
        LocalDateTime finNuevo = fechaHoraSolicitada.plusMinutes(30);
        
        // Obtener citas existentes del veterinario en esa fecha
        var citasDelDia = citaDAO.encontrarPorVeterinario(veterinarioId).stream()
            .filter(cita -> cita.getFechaHora().toLocalDate().equals(fechaHoraSolicitada.toLocalDate()))
            .filter(cita -> cita.getEstadoId() == 1 || cita.getEstadoId() == 2) // Solo programadas y confirmadas
            .toList();
        
        boolean disponible = true;
        String mensajeColision = "";
        
        for (var citaExistente : citasDelDia) {
            LocalDateTime inicioExistente = citaExistente.getFechaHora();
            LocalDateTime finExistente = citaExistente.getFechaHora().plusMinutes(30);
            
            // Verificar solapamiento
            if (inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente)) {
                disponible = false;
                mensajeColision = String.format("Error: El Dr. %s NO está disponible. Ya tiene una cita programada de %s a %s.",
                    veterinario.getNombreCompleto(),
                    inicioExistente.format(DateTimeFormatter.ofPattern("HH:mm")),
                    finExistente.format(DateTimeFormatter.ofPattern("HH:mm")));
                break;
            }
        }
        
        if (disponible) {
            System.out.printf("El Dr. %s SÍ está disponible el %s a las %s.%n",
                veterinario.getNombreCompleto(),
                fechaHoraSolicitada.toLocalDate(),
                fechaHoraSolicitada.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            System.out.println(mensajeColision);
        }
        
        LoggerManager.logInfo("Disponibilidad verificada - Vet ID: " + veterinarioId + " - Fecha: " + fechaHoraSolicitada);
    }
}
