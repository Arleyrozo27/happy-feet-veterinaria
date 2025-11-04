package model.Veterinario;
import java.time.LocalDate;

/**
 *
 * @author arley
 */

public class Veterinario {
    private int id;
    private String nombreCompleto;
    private String documentoIdentidad;
    private String licenciaProfesional;
    private String especialidad;
    private String telefono;
    private String email;
    private LocalDate fechaContratacion;
    private boolean activo;
    
    // Constructores
    public Veterinario() {}
    
    public Veterinario(String nombreCompleto, String documentoIdentidad, String licenciaProfesional) {
        this.nombreCompleto = nombreCompleto;
        this.documentoIdentidad = documentoIdentidad;
        this.licenciaProfesional = licenciaProfesional;
        this.activo = true;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }
    
    public String getLicenciaProfesional() { return licenciaProfesional; }
    public void setLicenciaProfesional(String licenciaProfesional) { this.licenciaProfesional = licenciaProfesional; }
    
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) { this.fechaContratacion = fechaContratacion; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    @Override
    public String toString() {
        return String.format("Veterinario{id=%d, nombre='%s', especialidad='%s'}", 
            id, nombreCompleto, especialidad);
    }
}