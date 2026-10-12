/*
 */
package com.sigefar.model;

/**
 * Subclase que especializa a Producto para el rubro farmacológico.
 * 
 * PILAR DE LA POO: HERENCIA
 * - Extiende de 'Producto' utilizando la palabra clave 'extends'.
 * - Reutiliza la lógica del padre mediante 'super(...)'.
 */
public class Medicamento extends Producto {
    // Atributos privados propios de la categoría farmacéutica (Encapsulamiento)
    private String principioActivo;
    private boolean requiereReceta;
    private double porcentajeDescuentoObraSocial;

    public Medicamento(long idProducto, String codigoBarras, String nombreComercial, 
                       double precioVenta, int stockActual, int stockMinimo,
                       String principioActivo, boolean requiereReceta, 
                       double porcentajeDescuentoObraSocial) {
        
        // Paso 1: Inicializa los atributos heredados de Producto
        super(idProducto, codigoBarras, nombreComercial, precioVenta, stockActual, stockMinimo);
        
        // Paso 2: Inicializa los atributos propios del medicamento
        this.principioActivo = principioActivo;
        this.requiereReceta = requiereReceta;
        this.porcentajeDescuentoObraSocial = porcentajeDescuentoObraSocial;
    }
    
    /**
     * PILAR DE LA POO: POLIMORFISMO (@Override)
     * Sobrescribe el método abstracto de Producto aplicando la regla comercial
     * de descuento por cobertura de obra social o prepaga.
     */
    @Override
    public double calcularPrecioFinal() {
        double descuento = this.precioVenta * (this.porcentajeDescuentoObraSocial / 100.0);
        return Math.max(0.0, this.precioVenta - descuento);
    }

    public String getPrincipioActivo() { return principioActivo; }
    public boolean isRequiereReceta() { return requiereReceta; }
    public double getPorcentajeDescuentoObraSocial() { return porcentajeDescuentoObraSocial; }
}
