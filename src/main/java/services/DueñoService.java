package services;
import Dao.DueñoDAO;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import model.Dueno.Dueño;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class DueñoService {
    private DueñoDAO dueñoDAO = new DueñoDAO();
    private Scanner scanner = new Scanner(System.in);
    
    /**
     * REGLA DE NEGOCIO: Validar que el email sea único
     */
    public void registrarDueño() {
        System.out.println("\n--- REGISTRAR NUEVO DUEÑO ---");
        
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Documento identidad: ");
        String documento = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();
        
        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();
        
        System.out.print("Contacto emergencia: ");
        String contactoEmergencia = scanner.nextLine();
        
        // Validar que el documento no exista
        if (dueñoDAO.encontrarPorDocumento(documento).isPresent()) {
            System.out.println("- Error: Ya existe un dueño con ese documento");
            return;
        }
        
        // Validar que el email no exista (implementar método en DAO)
        // if (dueñoDAO.encontrarPorEmail(email).isPresent()) {
        //     System.out.println("- Error: Ya existe un dueño con ese email");
        //     return;
        // }
        
        Dueño nuevoDueño = new Dueño(nombre, documento, email);
        nuevoDueño.setTelefono(telefono);
        nuevoDueño.setDireccion(direccion);
        nuevoDueño.setContactoEmergencia(contactoEmergencia);
        
        int id = dueñoDAO.guardar(nuevoDueño);
        
        if (id > 0) {
            System.out.println("- Dueño registrado exitosamente con ID: " + id);
            LoggerManager.logInfo("Nuevo dueño registrado: " + nombre + " - ID: " + id);
        } else {
            System.out.println("- Error al registrar dueño");
            LoggerManager.logError("Error al registrar dueño: " + nombre);
        }
    }
    
    /**
     * REGLA DE NEGOCIO: No mostrar dueños inactivos
     */
    public void listarDueños() {
        System.out.println("\n--- LISTA DE DUEÑOS ---");
        List<Dueño> dueños = dueñoDAO.encontrarTodos();
        
        if (dueños.isEmpty()) {
            System.out.println("No hay dueños registrados.");
            return;
        }
        
        for (Dueño dueño : dueños) {
            System.out.printf("ID: %d | %s | %s | %s%n", 
                dueño.getId(), 
                dueño.getNombreCompleto(), 
                dueño.getDocumentoIdentidad(),
                dueño.getEmail());
        }
    }
    
    /**
     * REGLA DE NEGOCIO: Validar que el dueño existe antes de operaciones
     */
    public void buscarDueñoPorDocumento() {
        System.out.print("\nIngrese documento a buscar: ");
        String documento = scanner.nextLine();
        
        Optional<Dueño> dueño = dueñoDAO.encontrarPorDocumento(documento);
        
        if (dueño.isPresent()) {
            Dueño d = dueño.get();
            System.out.println("- DUEÑO ENCONTRADO:");
            System.out.println("   ID: " + d.getId());
            System.out.println("   Nombre: " + d.getNombreCompleto());
            System.out.println("   Documento: " + d.getDocumentoIdentidad());
            System.out.println("   Email: " + d.getEmail());
            System.out.println("   Teléfono: " + d.getTelefono());
            System.out.println("   Dirección: " + d.getDireccion());
            System.out.println("   Contacto emergencia: " + d.getContactoEmergencia());
            System.out.println("   Fecha registro: " + d.getFechaRegistro());
        } else {
            System.out.println("- No se encontró dueño con documento: " + documento);
        }
    }
}
