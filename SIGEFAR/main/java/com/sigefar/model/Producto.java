/*
 */

package com.sigefar.model;

import com.sigefar.exception.FarmaciaExcepcion;

/**
 * Superclase abstracta que modela el concepto general de un producto comercializable.
 * 
 * PILAR DE LA POO: ABSTRACCIÓN
 * - Al ser abstracta, no puede instanciarse directamente mediante 'new Producto(...)'.
 * - Define el contrato común que deben cumplir todas las categorías de productos.
 */

public abstract class Producto {
    // Modificador 'protected': Permite que las subclases hijas (Medicamento y ArticuloPerfumeria)
    // accedan a estos atributos sin romper el encapsulamiento hacia el exterior.
    protected long idProducto;
    protected String codigoBarras;
    protected String nombreComercial;
    protected double precioVenta;
    protected int stockActual;
    protected int stockMinimo;
    
   /**
     * Constructor protegido para inicializar los datos comunes desde las subclases.
     */

    public Producto(long idProducto, String codigoBarras, String nombreComercial, 
                    double precioVenta, int stockActual, int stockMinimo) {
        this.idProducto = idProducto;
        this.codigoBarras = codigoBarras;
        this.nombreComercial = nombreComercial;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    /**
     * PILAR DE LA POO: POLIMORFISMO Y ABSTRACCIÓN
     * Método abstracto que obliga a cada subclase a implementar su propia lógica
     * para calcular el precio final (descuentos de obra social, recargos impositivos, etc.).
     */
    
    public abstract double calcularPrecioFinal();
    
    /**
     * Verifica si la cantidad demandada está cubierta por las existencias físicas en mostrador.
     */
    public boolean verificarStock(int cantidad) {
        return this.stockActual >= cantidad;
    }
    /**
     * Determina si la venta califica como "Venta por Encargo" a droguería (cuando falta stock).
     */
    public boolean esVentaPorEncargo(int cantidad) {
        return cantidad > this.stockActual;
    }
    
    /**
     * REGLA DE NEGOCIO:
     * Reduce las existencias del producto. Conforme a las pautas de SIGEFAR,
     * permite saldos negativos para registrar ventas por encargo con compromiso de entrega.
     */
    public void descontarStock(int cantidad) throws FarmaciaExcepcion {
        if (cantidad <= 0) {
            throw new FarmaciaExcepcion("La cantidad a descontar debe ser mayor a cero.");
        }
        this.stockActual -= cantidad; // Puede quedar en valor negativo (-1, -2, etc.)
    }
    
    /**
     * Incrementa el inventario cuando ingresa mercadería de droguería.
     */
    public void incrementarStock(int cantidad) {
        this.stockActual += cantidad;
    }
    
    // Getters para acceso seguro a los atributos (Encapsulamiento)
    public long getIdProducto() { return idProducto; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getNombreComercial() { return nombreComercial; }
    public double getPrecioVenta() { return precioVenta; }
    public int getStockActual() { return stockActual; }
    public int getStockMinimo() { return stockMinimo; }

    @Override
    public String toString() {
        return nombreComercial + " - Base: $" + precioVenta + " | Final: $" + calcularPrecioFinal() + " [Stock: " + stockActual + "]";
    }
}