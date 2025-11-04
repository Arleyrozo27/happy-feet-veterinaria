package model.Factura;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.ItemFactura.ItemFactura;

/**
 *
 * @author arley
 */

public class Factura {
    private int id;
    private int dueñoId;
    private String numeroFactura;
    private LocalDateTime fechaEmision;
    private double subtotal;
    private double impuesto;
    private double descuento;
    private double total;
    private String metodoPago;
    private String estado;
    private String observaciones;
    private List<ItemFactura> items;
    
    // Constructores
    public Factura() {
        this.items = new ArrayList<>();
        this.fechaEmision = LocalDateTime.now();
        this.estado = "Pendiente";
    }
    
    public Factura(int dueñoId, String numeroFactura) {
        this();
        this.dueñoId = dueñoId;
        this.numeroFactura = numeroFactura;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getDueñoId() { return dueñoId; }
    public void setDueñoId(int dueñoId) { this.dueñoId = dueñoId; }
    
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getImpuesto() { return impuesto; }
    public void setImpuesto(double impuesto) { this.impuesto = impuesto; }
    
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public List<ItemFactura> getItems() { return items; }
    public void setItems(List<ItemFactura> items) { this.items = items; }
    
    public void agregarItem(ItemFactura item) {
        this.items.add(item);
    }
    
    // Calcular totales automáticamente
    public void calcularTotales() {
        this.subtotal = items.stream().mapToDouble(ItemFactura::getSubtotal).sum();
        this.impuesto = this.subtotal * 0.19; // 19% de IVA
        this.total = this.subtotal + this.impuesto - this.descuento;
    }
}
