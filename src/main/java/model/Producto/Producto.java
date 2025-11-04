package model.Producto;
import java.time.LocalDate;

/**
 *
 * @author arley
 */

public class Producto {
    private int id;
    private String nombreProducto;
    private int productoTipoId;
    private String descripcion;
    private String fabricante;
    private Integer proveedorId;
    private String lote;
    private int cantidadStock;
    private int stockMinimo;
    private String unidadMedida;
    private LocalDate fechaVencimiento;
    private Double precioCompra;
    private Double precioVenta;
    private boolean requiereReceta;
    private boolean activo;
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombreProducto, int productoTipoId, double precioVenta) {
        this.nombreProducto = nombreProducto;
        this.productoTipoId = productoTipoId;
        this.precioVenta = precioVenta;
        this.cantidadStock = 0;
        this.stockMinimo = 5;
        this.unidadMedida = "unidad";
        this.activo = true;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    
    public int getProductoTipoId() { return productoTipoId; }
    public void setProductoTipoId(int productoTipoId) { this.productoTipoId = productoTipoId; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    
    public int getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(int cantidadStock) { this.cantidadStock = cantidadStock; }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }
    
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    
    public boolean isRequiereReceta() { return requiereReceta; }
    public void setRequiereReceta(boolean requiereReceta) { this.requiereReceta = requiereReceta; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    // Método de negocio: Verificar si está por debajo del stock mínimo
    public boolean necesitaReabastecimiento() {
        return cantidadStock <= stockMinimo;
    }
    
    // Método de negocio: Verificar si está próximo a vencer (30 días)
    public boolean estaProximoAVencer() {
        if (fechaVencimiento == null) return false;
        return LocalDate.now().plusDays(30).isAfter(fechaVencimiento);
    }
    
    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', stock=%d, precio=%.2f}", 
            id, nombreProducto, cantidadStock, precioVenta);
    }
}
