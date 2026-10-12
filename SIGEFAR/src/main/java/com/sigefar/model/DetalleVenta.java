/*
 */

package com.sigefar.model;

/**
 * Modela cada renglón del comprobante comercial de venta.
 * 
 * PILAR DE LA POO: ASOCIACIÓN Y POLIMORFISMO
 * - Mantiene una referencia directa al objeto 'Producto', sin acoplarse
 *   a si es un Medicamento o un ArticuloPerfumeria.
 */

public class DetalleVenta {
    private Producto producto;
    private long idProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String estadoEntrega; // "Entregado Inmediato" o "Pendiente de Entrega / Encargo"

    /**
     * Constructor orientado a objetos (TP3):
     * Calcula automáticamente el precio unitario invocando polimórficamente
     * a producto.calcularPrecioFinal().
     */
    public DetalleVenta(Producto producto, int cantidad, String estadoEntrega) {
        this.producto = producto;
        this.idProducto = (producto != null) ? producto.getIdProducto() : 0;
        this.cantidad = cantidad;
        this.precioUnitario = (producto != null) ? producto.calcularPrecioFinal() : 0.0;
        this.subtotal = this.cantidad * this.precioUnitario;
        this.estadoEntrega = estadoEntrega;
    }

    /**
     * Constructor de compatibilidad con VentanaVenta (para carga directa con valores precalculados).
     */
    public DetalleVenta(long idProducto, int cantidad, double precioUnitario, String estadoEntrega) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
        this.estadoEntrega = estadoEntrega;
    }

    public double calcularSubtotal() {
        return this.cantidad * this.precioUnitario;
    }

    public Producto getProducto() { return producto; }
    public long getIdProducto() { return (producto != null) ? producto.getIdProducto() : idProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return (subtotal > 0) ? subtotal : calcularSubtotal(); }
    public String getEstadoEntrega() { return estadoEntrega; }
}