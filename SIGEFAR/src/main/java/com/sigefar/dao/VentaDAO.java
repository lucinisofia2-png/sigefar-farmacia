/*
 */

package com.sigefar.dao;

import com.sigefar.model.Producto;
import com.sigefar.model.DetalleVenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public List<Producto> obtenerCatalogoActivo() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE estado = TRUE";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Producto(
                    rs.getLong("id_producto"),
                    rs.getString("codigo_barras"),
                    rs.getString("nombre_comercial"),
                    rs.getString("principio_activo"),
                    rs.getDouble("precio_venta"),
                    rs.getInt("stock_actual"),
                    rs.getInt("stock_minimo"),
                    rs.getBoolean("requiere_receta")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrarVenta(double total, String medioPago, int idUsuario, List<DetalleVenta> detalles) {
        Connection con = null;
        try {
            con = ConexionBD.getInstancia().getConexion();
            con.setAutoCommit(false); // INICIO TRANSACCIÓN ATÓMICA (ACID)

            // 1. Insertar Venta
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

            // 2. Insertar Comprobante
            String sqlComp = "INSERT INTO comprobante (nro_comprobante, id_venta) VALUES (?, ?)";
            try (PreparedStatement psComp = con.prepareStatement(sqlComp)) {
                psComp.setString(1, "TK-" + System.currentTimeMillis());
                psComp.setLong(2, idVentaGenerada);
                psComp.executeUpdate();
            }

            // 3. Procesar Líneas, Descontar Stock y Encolar Pedido a Droguería
            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal, estado_entrega) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ?";
            String sqlCheck = "SELECT stock_actual, stock_minimo FROM producto WHERE id_producto = ?";
            String sqlPedido = "INSERT INTO item_pedido_drogueria (id_pedido, id_producto, cantidad_solicitada, motivo) VALUES (1, ?, ?, ?)";

            for (DetalleVenta d : detalles) {
                try (PreparedStatement psDet = con.prepareStatement(sqlDetalle)) {
                    psDet.setLong(1, idVentaGenerada);
                    psDet.setLong(2, d.getIdProducto());
                    psDet.setInt(3, d.getCantidad());
                    psDet.setDouble(4, d.getPrecioUnitario());
                    psDet.setDouble(5, d.getSubtotal());
                    psDet.setString(6, d.getEstadoEntrega());
                    psDet.executeUpdate();
                }

                // Descuento en inventario (puede quedar saldo negativo)
                try (PreparedStatement psStk = con.prepareStatement(sqlStock)) {
                    psStk.setInt(1, d.getCantidad());
                    psStk.setLong(2, d.getIdProducto());
                    psStk.executeUpdate();
                }

                // Evaluar reposición a droguería
                try (PreparedStatement psChk = con.prepareStatement(sqlCheck)) {
                    psChk.setLong(1, d.getIdProducto());
                    try (ResultSet rsChk = psChk.executeQuery()) {
                        if (rsChk.next()) {
                            int stockRestante = rsChk.getInt("stock_actual");
                            int stockMin = rsChk.getInt("stock_minimo");
                            if (stockRestante < 0) {
                                // Venta por encargo: pedido inmediato
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

            con.commit(); // CONFIRMACIÓN EXITOSA
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { /* ignorar */ }
            }
            System.err.println("Error en transacción de venta: " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException e) { /* ignorar */ }
            }
        }
    }
}
