/*
 */

package com.sigefar.model;

public class DetalleVenta {
    private long idProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String estadoEntrega; // "Entregado Inmediato" o "Pendiente de Entrega / Encargo"

    public DetalleVenta(long idProducto, int cantidad, double precioUnitario, String estadoEntrega) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
        this.estadoEntrega = estadoEntrega;
    }

    public long getIdProducto() { return idProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
    public String getEstadoEntrega() { return estadoEntrega; }
}
