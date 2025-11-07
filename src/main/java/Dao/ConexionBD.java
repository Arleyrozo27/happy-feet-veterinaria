package Dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.LoggerManager;

/**
 *
 * @author arley
 */

public class ConexionBD {
    private static Connection conexion = null;
    
    //**DEBES MODIFICAR ESTOS DATOS CON TUS CREDENCIALES**
    private static final String URL = "jdbc:mysql://localhost:3306/happy_feet_veterinaria";
    private static final String USER = "campus2023";
    private static final String PASSWORD = "campus2023";
    
    // Constructor privado para evitar instancias
    private ConexionBD() {}
    
    public static Connection getConexion() {
        if (conexion == null) {
            try {
                // Registrar el driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión establecida con éxito a: " + URL);
            } catch (ClassNotFoundException e) {
                Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, "Error: Driver MySQL no encontrado", e);
                throw new RuntimeException("Driver MySQL no encontrado. Verifica tu pom.xml", e);
            } catch (SQLException e) {
                Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, "Error al conectar con la BD", e);
                throw new RuntimeException("No se pudo conectar a la BD. Verifica:", e);
            }
        }
        return conexion;
    }
    
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, "Error al cerrar conexión", e);
            }
        }
    }
    
    // Método para probar la conexión
    public static boolean probarConexion() {
        try (Connection conn = getConexion()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LoggerManager.logError("Error al probar conexión", e);
            return false;
        }
    }
}
