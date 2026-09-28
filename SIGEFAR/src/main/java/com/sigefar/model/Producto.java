/*
 */

package com.sigefar.model;

public class Producto {
    private long idProducto;
    private String codigoBarras;
    private String nombreComercial;
    private String principioActivo;
    private double precioVenta;
    private int stockActual;
    private int stockMinimo;
    private boolean requiereReceta;

    public Producto(long idProducto, String codigoBarras, String nombreComercial, String principioActivo, double precioVenta, int stockActual, int stockMinimo, boolean requiereReceta) {
        this.idProducto = idProducto;
        this.codigoBarras = codigoBarras;
        this.nombreComercial = nombreComercial;
        this.principioActivo = principioActivo;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.requiereReceta = requiereReceta;
    }

    public long getIdProducto() { return idProducto; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getNombreComercial() { return nombreComercial; }
    public String getPrincipioActivo() { return principioActivo; }
    public double getPrecioVenta() { return precioVenta; }
    public int getStockActual() { return stockActual; }
    public int getStockMinimo() { return stockMinimo; }
    public boolean isRequiereReceta() { return requiereReceta; }

    @Override
    public String toString() {
        return nombreComercial + " (" + principioActivo + ") - $" + precioVenta + " [Stock: " + stockActual + "]";
    }
}