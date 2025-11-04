package model.Raza;

/**
 *
 * @author arley
 */

public class Raza {
    private int id;
    private int especieId;
    private String nombre;
    private String caracteristicas;
    private String especieNombre; // Para mostrar en listados
    
    // Constructores
    public Raza() {}
    
    public Raza(int id, String nombre, String especieNombre) {
        this.id = id;
        this.nombre = nombre;
        this.especieNombre = especieNombre;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getEspecieId() { return especieId; }
    public void setEspecieId(int especieId) { this.especieId = especieId; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }
    
    public String getEspecieNombre() { return especieNombre; }
    public void setEspecieNombre(String especieNombre) { this.especieNombre = especieNombre; }
    
    @Override
    public String toString() {
        return String.format("Raza{id=%d, nombre='%s', especie='%s'}", 
            id, nombre, especieNombre);
    }
}
