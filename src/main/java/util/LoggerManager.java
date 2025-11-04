package util;

/**
 *
 * @author arley
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LoggerManager {
    
    public static void logError(String mensaje) {
        escribirLog("ERROR", mensaje);
    }
    
    public static void logInfo(String mensaje) {
        escribirLog("INFO", mensaje);
    }
    
    public static void logWarning(String mensaje) {
        escribirLog("WARNING", mensaje);
    }
    
    public static void logError(String mensaje, Exception ex) {
        escribirLog("ERROR", mensaje + " - " + ex.getMessage());
    }
    
    private static void escribirLog(String tipo, String mensaje) {
        FileWriter fw = null;
        try {
            // Usando Date (lo más básico)
            Date ahora = new Date();
            String registro = "[" + tipo + "] " + ahora + " - " + mensaje + "\n";
            
            // FileWriter con append=true
            fw = new FileWriter("log.txt", true);
            fw.write(registro);
            
            // También mostrar en consola
            System.out.print(registro);
            
        } catch (IOException e) {
            System.out.println("No se pudo escribir en el log: " + e.getMessage());
        } finally {
            // Cerrar el FileWriter en finally
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    System.out.println("Error cerrando FileWriter: " + e.getMessage());
                }
            }
        }
    }
}