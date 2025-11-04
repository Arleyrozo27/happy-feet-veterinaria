package model.Mascota;

/**
 *
 * @author arley
 */

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Mascota {
    private int id;
    private int dueñoId; // Cambiado de duenoId a dueñoId
    private String nombre;
    private int razaId;
    private LocalDate fechaNacimiento;
    private String sexo; // 'Macho', 'Hembra'
    private Double pesoActual;
    private String microchip;
    private String tatuaje;
    private String urlFoto;
    private String alergias;
    private String condicionesPreexistentes;
    private LocalDateTime fechaRegistro;
    private boolean activo;
    
    // Constructores
    public Mascota() {}
    
    public Mascota(int dueñoId, String nombre, int razaId, String sexo) {
        this.dueñoId = dueñoId;
        this.nombre = nombre;
        this.razaId = razaId;
        this.sexo = sexo;
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getDueñoId() { return dueñoId; } // Cambiado
    public void setDueñoId(int dueñoId) { this.dueñoId = dueñoId; } // Cambiado
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getRazaId() { return razaId; }
    public void setRazaId(int razaId) { this.razaId = razaId; }
    
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public Double getPesoActual() { return pesoActual; }
    public void setPesoActual(Double pesoActual) { this.pesoActual = pesoActual; }
    
    public String getMicrochip() { return microchip; }
    public void setMicrochip(String microchip) { this.microchip = microchip; }
    
    public String getTatuaje() { return tatuaje; }
    public void setTatuaje(String tatuaje) { this.tatuaje = tatuaje; }
    
    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }
    
    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }
    
    public String getCondicionesPreexistentes() { return condicionesPreexistentes; }
    public void setCondicionesPreexistentes(String condicionesPreexistentes) { this.condicionesPreexistentes = condicionesPreexistentes; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    @Override
    public String toString() {
        return String.format("Mascota{id=%d, nombre='%s', dueñoId=%d, sexo='%s'}", 
            id, nombre, dueñoId, sexo);
    }
}