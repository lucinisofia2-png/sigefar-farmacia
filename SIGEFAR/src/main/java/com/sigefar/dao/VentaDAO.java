/*
 */
package com.sigefar.dao;

import com.sigefar.model.Producto;
import com.sigefar.model.Medicamento;
import com.sigefar.model.DetalleVenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de Acceso a Datos (DAO) para las operaciones de venta e inventario.
 * 
 * Implementa control transaccional ACID mediante sentencias preparadas.
 */

public class VentaDAO {

    /**
     * Consulta el catálogo activo en MySQL.
     * Retorna objetos polimórficos de tipo Medicamento que heredan de Producto.
     */
    public List<Producto> obtenerCatalogoActivo() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE estado = TRUE";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Al ser Producto abstracta, se instancia la subclase concreta Medicamento
                lista.add(new Medicamento(
                    rs.getLong("id_producto"),
                    rs.getString("codigo_barras"),
                    rs.getString("nombre_comercial"),
                    rs.getDouble("precio_venta"),
                    rs.getInt("stock_actual"),
                    rs.getInt("stock_minimo"),
                    rs.getString("principio_activo") != null ? rs.getString("principio_activo") : "Venta Libre",
                    rs.getBoolean("requiere_receta"),
                    0.0 // Descuento de obra social base
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo: " + e.getMessage());
        }
        return lista;
    }
    
    /**
     * Ejecuta la transacción atómica de venta completa bajo propiedades ACID.
     * Si ocurre cualquier excepción en el proceso, ejecuta rollback() automático.
     */
    public boolean registrarVenta(double total, String medioPago, int idUsuario, List<DetalleVenta> detalles) {
        Connection con = null;
        try {
            con = ConexionBD.getInstancia().getConexion();
            // PASO 1: Inicia la transacción atómica desactivando el guardado automático
            con.setAutoCommit(false); 

            // PASO 2: Inserta el encabezado en la tabla 'venta' y recupera el ID autoincremental
            String sqlVenta = "INSERT INTO venta (total, medio_pago, id_usuario) VALUES (?, ?, ?)";
            long idVentaGenerada = 0;
            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setDouble(1, total);
                psVenta.setString(2, medioPago);
                psVenta.setInt(3, idUsuario);
                psVenta.executeUpdate();
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) idVentaGenerada = rs.getLong(1);
                }
            }

            // PASO 3: Inserta el comprobante comercial asociado unívocamente a la venta
            String sqlComp = "INSERT INTO comprobante (nro_comprobante, id_venta) VALUES (?, ?)";
            try (PreparedStatement psComp = con.prepareStatement(sqlComp)) {
                psComp.setString(1, "TK-" + System.currentTimeMillis());
                psComp.setLong(2, idVentaGenerada);
                psComp.executeUpdate();
            }

            // PASO 4: Itera los renglones, descuenta stock y encola pedidos por encargo
            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal, estado_entrega) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ?";
            String sqlCheck = "SELECT stock_actual, stock_minimo FROM producto WHERE id_producto = ?";
            String sqlPedido = "INSERT INTO item_pedido_drogueria (id_pedido, id_producto, cantidad_solicitada, motivo) VALUES (1, ?, ?, ?)";

            for (DetalleVenta d : detalles) {
                // Inserción de la línea del detalle
                try (PreparedStatement psDet = con.prepareStatement(sqlDetalle)) {
                    psDet.setLong(1, idVentaGenerada);
                    psDet.setLong(2, d.getIdProducto());
                    psDet.setInt(3, d.getCantidad());
                    psDet.setDouble(4, d.getPrecioUnitario());
                    psDet.setDouble(5, d.getSubtotal());
                    psDet.setString(6, d.getEstadoEntrega());
                    psDet.executeUpdate();
                }

                // Descuento en inventario físico (admite saldos negativos para encargo)
                try (PreparedStatement psStk = con.prepareStatement(sqlStock)) {
                    psStk.setInt(1, d.getCantidad());
                    psStk.setLong(2, d.getIdProducto());
                    psStk.executeUpdate();
                }

                // REGLA DE NEGOCIO: Si el stock quedó en saldo negativo, genera el pedido de reposición
                try (PreparedStatement psChk = con.prepareStatement(sqlCheck)) {
                    psChk.setLong(1, d.getIdProducto());
                    try (ResultSet rsChk = psChk.executeQuery()) {
                        if (rsChk.next()) {
                            int stockRestante = rsChk.getInt("stock_actual");
                            int stockMin = rsChk.getInt("stock_minimo");
                            if (stockRestante < 0) {
                                try (PreparedStatement psPed = con.prepareStatement(sqlPedido)) {
                                    psPed.setLong(1, d.getIdProducto());
                                    psPed.setInt(2, Math.abs(stockRestante) + stockMin);
                                    psPed.setString(3, "Venta por Encargo - Saldo Negativo (" + stockRestante + ")");
                                    psPed.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
            
            // PASO 5: Todas las operaciones fueron exitosas, confirma los cambios en la base de datos
            con.commit();
            return true;

        } catch (SQLException e) {
            // MANEJO DE ERROR: Si cualquier operación falla, deshace todos los cambios
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { /* ignorar */ }
            }
            System.err.println("Error en transacción de venta: " + e.getMessage());
            return false;
        } finally {
            // Restaura el autocommit por seguridad
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException e) { /* ignorar */ }
            }
        }
    }
}