/**
 * * Excepción personalizada para el dominio de la farmacia.
 * 
 * Hereda de java.lang.Exception para crear una excepción comprobada (checked).
 * Permite capturar e informar de forma controlada condiciones anómalas
 * del negocio (ej. cantidades inválidas, productos inexistentes o errores de recetas).
 */

package com.sigefar.exception;

public class FarmaciaExcepcion extends Exception {
    /**
     * Constructor que recibe el mensaje descriptivo del error.
     * @param mensaje Detalle del motivo por el cual se interrumpe el flujo normal.
     */
    public FarmaciaExcepcion(String mensaje) {
        super(mensaje); // Pasa el mensaje a la superclase Exception
    }
}
