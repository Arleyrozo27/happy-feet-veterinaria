package Dao;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author arley
 */

/**
 * Interfaz base para todos los DAOs (Data Access Object)
 * Define las operaciones CRUD básicas que deben implementar todos los DAOs
 * 
 * @param <T> El tipo de entidad que maneja el DAO (Dueño, Mascota, Producto, etc.)
 */
public interface BaseDAO<T> {
    
    /**
     * Busca una entidad por su ID
     * @param id El ID de la entidad a buscar
     * @return Optional que contiene la entidad si existe, o vacío si no existe
     */
    Optional<T> encontrarPorId(int id);
    
    /**
     * Obtiene todas las entidades de la base de datos
     * @return Lista con todas las entidades
     */
    List<T> encontrarTodos();
    
    /**
     * Guarda una nueva entidad en la base de datos
     * @param entidad La entidad a guardar
     * @return El ID generado para la nueva entidad, o -1 si hubo error
     */
    int guardar(T entidad);
    
    /**
     * Actualiza una entidad existente en la base de datos
     * @param entidad La entidad con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean actualizar(T entidad);
    
    /**
     * Elimina una entidad de la base de datos (generalmente soft delete)
     * @param id El ID de la entidad a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean eliminar(int id);
}