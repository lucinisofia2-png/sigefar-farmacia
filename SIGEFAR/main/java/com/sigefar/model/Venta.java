/*
 */
package com.sigefar.model;

import com.sigefar.exception.FarmaciaExcepcion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase contenedora y gestora del comprobante de venta.
 * 
 * Centraliza la colección dinámica de detalles y los cálculos del comprobante,
 * asegurando que la transacción mantenga datos válidos antes de persistirse.
 */

public class Venta {
    private long idVenta;
    private LocalDateTime fechaHora;
    private String medioPago;
    private List<DetalleVenta> detalles; // Estructura de datos dinámica (ArrayList)

    public Venta(long idVenta, String medioPago) {
        this.idVenta = idVenta;
        this.fechaHora = LocalDateTime.now();
        this.medioPago = medioPago;
        this.detalles = new ArrayList<>(); // Inicializa la lista vacía
    }
    
    /**
     * Agrega una línea de venta validando que no sea nula.
     */
    public void agregarDetalle(DetalleVenta detalle) throws FarmaciaExcepcion {
        if (detalle == null) {
            throw new FarmaciaExcepcion("No se puede agregar un detalle nulo al comprobante.");
        }
        this.detalles.add(detalle);
    }
    
    /**
     * Itera polimórficamente acumulando los subtotales de cada producto.
     */
    public double calcularTotal() {
        double total = 0.0;
        for (DetalleVenta d : detalles) {
            total += d.calcularSubtotal();
        }
        return total;
    }

    public boolean hayItems() {
        return !this.detalles.isEmpty();
    }
    
    /**
     * Emite por consola el ticket de venta formateado de manera profesional.
     */
    public void mostrarDetalleVenta() {
        System.out.println("==========================================================================");
        System.out.println(" COMPROBANTE DE VENTA Nro: " + idVenta + " | FECHA: " + fechaHora);
        System.out.println(" MEDIO DE PAGO: " + medioPago);
        System.out.println("--------------------------------------------------------------------------");
        for (DetalleVenta d : detalles) {
            String nombre = (d.getProducto() != null) ? d.getProducto().getNombreComercial() : ("Producto #" + d.getIdProducto());
            System.out.printf("%-30s | Cant: %2d | P.Unit: $%8.2f | Subt: $%8.2f | [%s]%n",
                    nombre,
                    d.getCantidad(),
                    d.getPrecioUnitario(),
                    d.calcularSubtotal(),
                    d.getEstadoEntrega());
        }
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" TOTAL A COBRAR: $%8.2f%n", calcularTotal());
        System.out.println("==========================================================================");
    }

    public long getIdVenta() { return idVenta; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMedioPago() { return medioPago; }
    public List<DetalleVenta> getDetalles() { return detalles; }
}