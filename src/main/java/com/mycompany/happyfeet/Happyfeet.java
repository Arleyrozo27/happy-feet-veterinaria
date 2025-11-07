package com.mycompany.happyfeet;
import Observer.ArchivoObserver;
import Observer.ConsolaObserver;
import Observer.NotificacionObserver;
import services.DueñoService;
import services.MascotaService;
import util.LoggerManager;
import java.util.Scanner;
import services.CitaService;
import services.ContratoAdopcionService;
import services.DisponibilidadVeterinarioService;
import services.FacturaService;
import services.HistorialPesoService;
import services.ProcedimientoService;
import services.ProductoService;
import services.ReporteProductosService;

/**
 *
 * @author arley
 */

public class Happyfeet {
    private static DueñoService dueñoService = new DueñoService();
    private static MascotaService mascotaService = new MascotaService();
    private static ProductoService productoService = new ProductoService();
    private static CitaService citaService = new CitaService();
    private static FacturaService facturaService = new FacturaService();
    private static HistorialPesoService historialPesoService = new HistorialPesoService();
    private static DisponibilidadVeterinarioService disponibilidadService = new DisponibilidadVeterinarioService();
    private static ContratoAdopcionService contratoService = new ContratoAdopcionService();
    private static ProcedimientoService procedimientoService = new ProcedimientoService();
    private static ReporteProductosService reporteProductosService = new ReporteProductosService();
    
    private static Scanner scanner = new Scanner(System.in);



    public static void main(String[] args) {
        LoggerManager.logInfo("Sistema Happy Feet iniciado");
        System.out.println("- BIENVENIDO AL SISTEMA HAPPY FEET VETERINARIA -");
        
        configurarObservers();
        
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    gestionarDueños();
                    break;
                case 2:
                    gestionarMascotas();
                    break;
                case 3:
                    gestionarCitas();
                    break;
                case 4:
                    gestionarFacturacion();
                    break;
                case 5:
                    gestionarInventario();
                    break;
                case 0:
                    continuar = false;
                    System.out.println("Gracias por usar Happy Feet!");
                    break;
                default:
                    System.out.println("Opcion invalida. Intenta nuevamente.");
            }
        }
        
        scanner.close();
        LoggerManager.logInfo("Sistema Happy Feet finalizado");
    }
    
    private static void configurarObservers() {
        System.out.println("Configurando sistema de notificaciones...");
        ConsolaObserver consolaObserver = new ConsolaObserver();
        ArchivoObserver archivoObserver = new ArchivoObserver();
        
        procedimientoService.registrarObservador(consolaObserver);
        procedimientoService.registrarObservador(archivoObserver);
        
        System.out.println("Sistema de notificaciones configurado correctamente");
    }
    
    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1.Gestion de Duenos");
        System.out.println("2.Gestion de Mascotas");
        System.out.println("3.Gestion de Citas");
        System.out.println("4.Facturación");
        System.out.println("5.Control de Inventario");
        System.out.println("0.Salir");
        System.out.print("Selecciona una opcion: ");
    }
    
    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void gestionarProcedimientos() {
        System.out.println("\n--- GESTIÓN DE PROCEDIMIENTOS ---");
        
        boolean volver = false;
        while (!volver) {
            System.out.println("\n1. Actualizar estado de procedimiento");
            System.out.println("2. Probar notificaciones manualmente");
            System.out.println("3. Volver al menú principal");
            System.out.print("Selecciona: ");
            
            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    System.out.print("ID del procedimiento: ");
                    int procId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Nuevo estado (Programado/En Proceso/Finalizado/Cancelado): ");
                    String estado = scanner.nextLine();
                    procedimientoService.actualizarEstadoProcedimiento(procId, estado);
                    break;
                case 2:
                    procedimientoService.probarNotificacionManual();
                    break;
                case 3:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }
    
    private static void gestionarDueños() {
        System.out.println("\n--- GESTION DE DUENOS ---");
        
        boolean volver = false;
        while (!volver) {
            System.out.println("\n1.Registrar dueno");
            System.out.println("2.Listar duenos");
            System.out.println("3.Buscar dueno por documento");
            System.out.println("4.Volver al menu principal");
            System.out.print("Selecciona: ");
            
            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    dueñoService.registrarDueño();
                    break;
                case 2:
                    dueñoService.listarDueños();
                    break;
                case 3:
                    dueñoService.buscarDueñoPorDocumento();
                    break;
                case 4:
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }
    
    private static void gestionarMascotas() {
        System.out.println("\n--- GESTION DE MASCOTAS ---");
        
        boolean volver = false;
        while (!volver) {
            System.out.println("\n1.Registrar mascota");
            System.out.println("2.Listar todas las mascotas");
            System.out.println("3.Listar mascotas por dueno");
            System.out.println("4.Buscar mascota por nombre");
            System.out.println("5.Ver razas disponibles");
            System.out.println("0.Volver al menu principal");
            System.out.print("Selecciona: ");
            
            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    mascotaService.registrarMascota();
                    break;
                case 2:
                    mascotaService.listarTodasLasMascotas();
                    break;
                case 3:
                    mascotaService.listarMascotasPorDueño();
                    break;
                case 4:
                    mascotaService.buscarMascotaPorNombre();
                    break;
                case 5:
                    mascotaService.mostrarRazasDisponibles();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }
    
    private static void gestionarCitas() {
        System.out.println("\n--- GESTION DE CITAS ---");
        
        boolean volver = false;
        while (!volver) {
            System.out.println("\n1.Agendar nueva cita");
            System.out.println("2.Listar todas las citas");
            System.out.println("3.Listar citas por mascota");
            System.out.println("4.Listar citas por veterinario");
            System.out.println("5.Listar citas por estado");
            System.out.println("6.Listar veterinarios disponibles");
            System.out.println("7.Ver disponibilidad por fecha");
            System.out.println("8.Cambiar estado de cita");
            System.out.println("9.Volver al menu principal");
            System.out.print("Selecciona: ");
            
            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    citaService.agendarCita();
                    break;
                case 2:
                    citaService.listarCitas();
                    break;
                case 3:
                    citaService.listarCitasPorMascota();
                    break;
                case 4:
                    citaService.listarCitasPorVeterinario();
                    break;
                case 5:
                    citaService.listarCitasPorEstado();
                    break;
                case 6:
                    citaService.listarVeterinarios();
                    break;
                case 7:
                    citaService.verDisponibilidadVeterinarios();
                break;
                case 8:
                    citaService.cambiarEstadoCita();
                    break;
                case 9:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción invalida");
            }
        }
    }
    
    private static void gestionarFacturacion() {
        System.out.println("\n--- FACTURACIÓN ---");

        boolean volver = false;
        while (!volver) {
            System.out.println("\n1.Generar factura rápida");
            System.out.println("2.Volver al menú principal");
            System.out.print("Selecciona: ");

            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    facturaService.generarFacturaRapida();
                    break;
                case 2:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }
    
    private static void gestionarInventario() {
        System.out.println("\n--- CONTROL DE INVENTARIO ---");
        
        boolean volver = false;
        while (!volver) {
            System.out.println("\n1.Registrar producto");
            System.out.println("2.Listar inventario completo");
            System.out.println("3.Buscar producto"); 
            System.out.println("4.Descontar stock");
            System.out.println("5.Productos proximos a vencer");
            System.out.println("6.Probar Notificaciones");
            System.out.println("0.Volver al menu principal");
            System.out.print("Selecciona: ");
            
            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    productoService.registrarProducto();
                    break;
                case 2:
                    productoService.listarProductos();
                    break;
                case 3:
                    productoService.buscarProducto();
                    break;
                case 4:
                    productoService.descontarStock();
                    break;
                case 5:
                    productoService.mostrarProductosProximosAVencer();
                    break;
                case 6:
                    gestionarProcedimientos();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }
}
