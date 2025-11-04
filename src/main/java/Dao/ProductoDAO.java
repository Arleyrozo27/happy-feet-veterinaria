package Dao;

/**
 *
 * @author arley
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Producto.Producto;
import util.LoggerManager;

public class ProductoDAO implements BaseDAO<Producto> {
    
    @Override
    public Optional<Producto> encontrarPorId(int id) {
        String sql = "SELECT * FROM inventario WHERE id = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearProducto(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al buscar producto por ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return Optional.empty();
    }
    
    @Override
    public List<Producto> encontrarTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE activo = TRUE ORDER BY nombre_producto";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener todos los productos", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return productos;
    }
    
    @Override
    public int guardar(Producto producto) {
        String sql = "INSERT INTO inventario (nombre_producto, producto_tipo_id, descripcion, fabricante, proveedor_id, lote, cantidad_stock, stock_minimo, unidad_medida, fecha_vencimiento, precio_compra, precio_venta, requiere_receta) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, producto.getNombreProducto());
            stmt.setInt(2, producto.getProductoTipoId());
            stmt.setString(3, producto.getDescripcion());
            stmt.setString(4, producto.getFabricante());
            
            if (producto.getProveedorId() != null) {
                stmt.setInt(5, producto.getProveedorId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setString(6, producto.getLote());
            stmt.setInt(7, producto.getCantidadStock());
            stmt.setInt(8, producto.getStockMinimo());
            stmt.setString(9, producto.getUnidadMedida());
            
            if (producto.getFechaVencimiento() != null) {
                stmt.setDate(10, Date.valueOf(producto.getFechaVencimiento()));
            } else {
                stmt.setNull(10, Types.DATE);
            }
            
            if (producto.getPrecioCompra() != null) {
                stmt.setDouble(11, producto.getPrecioCompra());
            } else {
                stmt.setNull(11, Types.DECIMAL);
            }
            
            stmt.setDouble(12, producto.getPrecioVenta());
            stmt.setBoolean(13, producto.isRequiereReceta());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al guardar producto: " + producto.getNombreProducto(), e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return -1;
    }
    
    @Override
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE inventario SET nombre_producto = ?, producto_tipo_id = ?, descripcion = ?, fabricante = ?, proveedor_id = ?, lote = ?, cantidad_stock = ?, stock_minimo = ?, unidad_medida = ?, fecha_vencimiento = ?, precio_compra = ?, precio_venta = ?, requiere_receta = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, producto.getNombreProducto());
            stmt.setInt(2, producto.getProductoTipoId());
            stmt.setString(3, producto.getDescripcion());
            stmt.setString(4, producto.getFabricante());
            
            if (producto.getProveedorId() != null) {
                stmt.setInt(5, producto.getProveedorId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setString(6, producto.getLote());
            stmt.setInt(7, producto.getCantidadStock());
            stmt.setInt(8, producto.getStockMinimo());
            stmt.setString(9, producto.getUnidadMedida());
            
            if (producto.getFechaVencimiento() != null) {
                stmt.setDate(10, Date.valueOf(producto.getFechaVencimiento()));
            } else {
                stmt.setNull(10, Types.DATE);
            }
            
            if (producto.getPrecioCompra() != null) {
                stmt.setDouble(11, producto.getPrecioCompra());
            } else {
                stmt.setNull(11, Types.DECIMAL);
            }
            
            stmt.setDouble(12, producto.getPrecioVenta());
            stmt.setBoolean(13, producto.isRequiereReceta());
            stmt.setInt(14, producto.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al actualizar producto ID: " + producto.getId(), e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE inventario SET activo = FALSE WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al eliminar producto ID: " + id, e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    // MÉTODOS ESPECÍFICOS PARA INVENTARIO
    
    public List<Producto> encontrarConStockBajo() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE cantidad_stock <= stock_minimo AND activo = TRUE ORDER BY cantidad_stock ASC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener productos con stock bajo", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return productos;
    }
    
    public List<Producto> encontrarProximosAVencer() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE fecha_vencimiento IS NOT NULL AND fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND activo = TRUE ORDER BY fecha_vencimiento ASC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            LoggerManager.logError("Error al obtener productos próximos a vencer", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return productos;
    }
    
    public boolean descontarStock(int productoId, int cantidad) {
        String sql = "UPDATE inventario SET cantidad_stock = cantidad_stock - ? WHERE id = ? AND cantidad_stock >= ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexionBD.getConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cantidad);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerManager.logError("Error al descontar stock del producto ID: " + productoId, e);
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
        return false;
    }
    
    // Método privado para mapear ResultSet a objeto Producto
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombreProducto(rs.getString("nombre_producto"));
        producto.setProductoTipoId(rs.getInt("producto_tipo_id"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setFabricante(rs.getString("fabricante"));
        
        int proveedorId = rs.getInt("proveedor_id");
        if (!rs.wasNull()) {
            producto.setProveedorId(proveedorId);
        }
        
        producto.setLote(rs.getString("lote"));
        producto.setCantidadStock(rs.getInt("cantidad_stock"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        producto.setUnidadMedida(rs.getString("unidad_medida"));
        
        Date fechaVencimiento = rs.getDate("fecha_vencimiento");
        if (fechaVencimiento != null) {
            producto.setFechaVencimiento(fechaVencimiento.toLocalDate());
        }
        
        double precioCompra = rs.getDouble("precio_compra");
        if (!rs.wasNull()) {
            producto.setPrecioCompra(precioCompra);
        }
        
        producto.setPrecioVenta(rs.getDouble("precio_venta"));
        producto.setRequiereReceta(rs.getBoolean("requiere_receta"));
        producto.setActivo(rs.getBoolean("activo"));
        
        return producto;
    }
    
    private void cerrarRecursos(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LoggerManager.logError("Error al cerrar recursos", e);
        }
    }
}