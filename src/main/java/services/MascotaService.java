package services;
import Dao.DueñoDAO;
import Dao.MascotaDAO;
import Dao.RazaDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import model.Dueno.Dueño;
import model.Mascota.Mascota;
import model.Raza.Raza;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class MascotaService {
    private MascotaDAO mascotaDAO = new MascotaDAO();
    private DueñoDAO dueñoDAO = new DueñoDAO();
    private Scanner scanner = new Scanner(System.in);

    /**
     * REGLA DE NEGOCIO CRÍTICA: Validar que el dueño existe
     */
    
    public void registrarMascota() {
        System.out.println("\n--- REGISTRAR NUEVA MASCOTA ---");
        
        // Primero validar que el dueño existe
        System.out.print("ID del dueño: ");
        int dueñoId;
        try {
            dueñoId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: ID debe ser un número");
            return;
        }
        
        // REGLA DE NEGOCIO: Verificar que el dueño existe y está activo
        Optional<Dueño> dueño = dueñoDAO.encontrarPorId(dueñoId);
        if (dueño.isEmpty()) {
            System.out.println("Error: No existe un dueño con ID " + dueñoId);
            return;
        }
        
        System.out.println("Dueño: " + dueño.get().getNombreCompleto());
        
        System.out.print("Nombre de la mascota: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Raza ID (1=Labrador, 2=Golden, etc.): ");
        int razaId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Sexo (Macho/Hembra): ");
        String sexo = scanner.nextLine();
        
        System.out.print("Fecha nacimiento (YYYY-MM-DD) o Enter para omitir: ");
        String fechaStr = scanner.nextLine();
        
        System.out.print("Peso actual: ");
        double peso = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Microchip (opcional): ");
        String microchip = scanner.nextLine();
        
        System.out.print("Alergias (opcional): ");
        String alergias = scanner.nextLine();
        
        Mascota nuevaMascota = new Mascota(dueñoId, nombre, razaId, sexo);
        nuevaMascota.setPesoActual(peso);
        nuevaMascota.setMicrochip(microchip);
        nuevaMascota.setAlergias(alergias);
        
        if (!fechaStr.isEmpty()) {
            nuevaMascota.setFechaNacimiento(LocalDate.parse(fechaStr));
        }
        
        int id = mascotaDAO.guardar(nuevaMascota);
        
        if (id > 0) {
            System.out.println("Mascota registrada exitosamente con ID: " + id);
            LoggerManager.logInfo("Nueva mascota registrada: " + nombre + " - Dueño ID: " + dueñoId);
        } else {
            System.out.println("Error al registrar mascota");
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Mostrar mascotas de un dueño específico
     */
    public void listarMascotasPorDueño() {
        System.out.print("\nID del dueño: ");
        int dueñoId = Integer.parseInt(scanner.nextLine());
        
        // Validar que el dueño existe
        Optional<Dueño> dueño = dueñoDAO.encontrarPorId(dueñoId);
        if (dueño.isEmpty()) {
            System.out.println("No existe dueño con ID " + dueñoId);
            return;
        }
        
        System.out.println("Mascotas de: " + dueño.get().getNombreCompleto());
        List<Mascota> mascotas = mascotaDAO.encontrarPorDueño(dueñoId);
        
        if (mascotas.isEmpty()) {
            System.out.println("No tiene mascotas registradas.");
            return;
        }
        
        for (Mascota mascota : mascotas) {
            System.out.printf(" %s | %s | %.1f kg | Nac: %s%n",
                mascota.getNombre(),
                mascota.getSexo(),
                mascota.getPesoActual(),
                mascota.getFechaNacimiento() != null ? mascota.getFechaNacimiento() : "No registrada");
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Listar todas las mascotas con información de dueño
     */
    public void listarTodasLasMascotas() {
        System.out.println("\n--- TODAS LAS MASCOTAS ---");
        List<Mascota> mascotas = mascotaDAO.encontrarTodos();
        
        if (mascotas.isEmpty()) {
            System.out.println("No hay mascotas registradas.");
            return;
        }
        
        for (Mascota mascota : mascotas) {
            String nombreDueño = obtenerNombreDueño(mascota.getDueñoId());
            String nombreRaza = obtenerNombreRaza(mascota.getRazaId());
            
            System.out.printf("ID: %d | %s | Dueño: %s | Raza: %s | Sexo: %s | Peso: %.1f kg%n",
                mascota.getId(),
                mascota.getNombre(),
                nombreDueño,
                nombreRaza,
                mascota.getSexo(),
                mascota.getPesoActual() != null ? mascota.getPesoActual() : 0.0);
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Buscar mascota por nombre
     */
    public void buscarMascotaPorNombre() {
        System.out.print("\nIngrese nombre o parte del nombre de la mascota: ");
        String busqueda = scanner.nextLine().toLowerCase();
        
        List<Mascota> mascotas = mascotaDAO.encontrarTodos();
        List<Mascota> resultados = mascotas.stream()
            .filter(m -> m.getNombre().toLowerCase().contains(busqueda))
            .toList();
        
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron mascotas con: " + busqueda);
            return;
        }
        
        System.out.println("Mascotas encontradas (" + resultados.size() + "):");
        for (Mascota mascota : resultados) {
            String nombreDueño = obtenerNombreDueño(mascota.getDueñoId());
            System.out.printf(" ID: %d | %s | Dueño: %s | Sexo: %s%n",
                mascota.getId(),
                mascota.getNombre(),
                nombreDueño,
                mascota.getSexo());
        }
    }
    
    // MÉTODOS AUXILIARES
    
    /**
     * Obtiene el nombre del dueño por su ID
     */
    
    private String obtenerNombreDueño(int dueñoId) {
        DueñoDAO dueñoDAO = new DueñoDAO();
        var dueñoOpt = dueñoDAO.encontrarPorId(dueñoId);
        return dueñoOpt.map(dueño -> dueño.getNombreCompleto()).orElse("Dueño desconocido");
    }
    
    /**
    * REGLA DE NEGOCIO: Mostrar razas disponibles para registro
    */
    public void mostrarRazasDisponibles() {
       System.out.println("\n--- RAZAS DISPONIBLES ---");
        RazaDAO razaDAO = new RazaDAO();
       List<Raza> razas = razaDAO.encontrarTodos();

       if (razas.isEmpty()) {
           System.out.println("No hay razas registradas en el sistema.");
           return;
       }

       String especieActual = "";
       for (Raza raza : razas) {
           // Agrupar por especie
           if (!raza.getEspecieNombre().equals(especieActual)) {
               especieActual = raza.getEspecieNombre();
               System.out.println("\n🐾 " + especieActual.toUpperCase() + ":");
           }

           System.out.printf("   ID: %d | %s", raza.getId(), raza.getNombre());
           if (raza.getCaracteristicas() != null && !raza.getCaracteristicas().isEmpty()) {
               System.out.printf(" - %s", raza.getCaracteristicas());
           }
           System.out.println();
       }

       System.out.println("\n💡 Usa estos IDs al registrar una nueva mascota");
    }
    
    /**
     * Obtiene el nombre de la raza por su ID  
     */
    private String obtenerNombreRaza(int razaId) {
        // Por ahora retornamos el ID, luego puedes implementar la consulta a la tabla razas
        return "Raza ID: " + razaId;
        // Para implementar completo necesitaríamos un RazaDAO
    }
}