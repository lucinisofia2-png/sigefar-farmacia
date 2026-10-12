/*
 */
package com.sigefar.model;

/**
 * Subclase que especializa a Producto para higiene, tocador y cosmética.
 * 
 * PILAR DE LA POO: HERENCIA Y POLIMORFISMO
 * - Extiende de 'Producto' y redefine la fórmula de precio con recargo/impuesto comercial.
 */

public class ArticuloPerfumeria extends Producto {
    private String categoriaPerfumeria;
    private String contenidoNeto;
    private double tasaImpuestoAdicional;

    public ArticuloPerfumeria(long idProducto, String codigoBarras, String nombreComercial, 
                              double precioVenta, int stockActual, int stockMinimo,
                              String categoriaPerfumeria, String contenidoNeto, 
                              double tasaImpuestoAdicional) {
        super(idProducto, codigoBarras, nombreComercial, precioVenta, stockActual, stockMinimo);
        this.categoriaPerfumeria = categoriaPerfumeria;
        this.contenidoNeto = contenidoNeto;
        this.tasaImpuestoAdicional = tasaImpuestoAdicional;
    }
    
    /**
     * PILAR DE LA POO: POLIMORFISMO (@Override)
     * Aplica la fórmula comercial específica de perfumería sumando la tasa impositiva.
     */
    @Override
    public double calcularPrecioFinal() {
        double recargo = this.precioVenta * (this.tasaImpuestoAdicional / 100.0);
        return this.precioVenta + recargo;
    }

    public String getCategoriaPerfumeria() { return categoriaPerfumeria; }
    public String getContenidoNeto() { return contenidoNeto; }
    public double getTasaImpuestoAdicional() { return tasaImpuestoAdicional; }
}
