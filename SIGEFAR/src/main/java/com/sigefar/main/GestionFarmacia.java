/*
 */
package com.sigefar.main;

import com.sigefar.exception.FarmaciaExcepcion;
import com.sigefar.model.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal ejecutable provista de un menú interactivo por consola.
 * 
 * ESTRUCTURAS DE CONTROL UTILIZADAS:
 * - Bucle repetitivo 'while(true)' para mantener el menú activo.
 * - Estructura condicional 'switch(opcion)' para derivar la acción elegida.
 * - Tratamiento de excepciones con bloques 'try-catch' para capturar
 *   errores de ingreso ('InputMismatchException') y reglas de negocio ('FarmaciaExcepcion').
 */
public class GestionFarmacia {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Producto> inventario = new ArrayList<>(); // Colección dinámica polimórfica

        // PASO 1: Carga inicial de datos de prueba demostrando POLIMORFISMO
        inventario.add(new Medicamento(1, "7791234567890", "Amoxidal 500mg x 16", 8500.0, 15, 5, "Amoxicilina", true, 20.0));
        inventario.add(new Medicamento(2, "7791234567891", "Ibupirac 600mg x 10", 4200.0, 2, 8, "Ibuprofeno", false, 10.0));
        inventario.add(new ArticuloPerfumeria(3, "7791234567892", "Shampoo Anticaspa 400ml", 5900.0, 20, 4, "Cuidado Capilar", "400 ml", 5.0));

        Venta ventaActiva = new Venta(1001, "Efectivo");
        
        // PASO 2: Ciclo de vida del menú interactivo
        while (true) {
            System.out.println("\n==============================================");
            System.out.println("   SISTEMA DE GESTION Y FACTURACION (SIGEFAR) ");
            System.out.println("==============================================");
            System.out.println("1. Registrar nuevo Medicamento");
            System.out.println("2. Registrar nuevo Articulo de Perfumeria");
            System.out.println("3. Listar catalogo y existencias");
            System.out.println("4. Iniciar venta y agregar producto al carrito");
            System.out.println("5. Calcular total polimorfico y confirmar venta");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opcion (1-6): ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpieza de buffer

                switch (opcion) {
                    case 1:
                        // ALTA DE MEDICAMENTO CON ENCAPSULAMIENTO
                        System.out.print("Nombre Comercial: ");
                        String nomMed = scanner.nextLine();
                        System.out.print("Precio de Venta Base: ");
                        double preMed = scanner.nextDouble();
                        System.out.print("Stock Inicial: ");
                        int stkMed = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Principio Activo: ");
                        String droga = scanner.nextLine();
                        System.out.print("Porcentaje Cobertura Obra Social (%): ");
                        double desc = scanner.nextDouble();
                        inventario.add(new Medicamento(inventario.size() + 1, "MED-" + System.currentTimeMillis(), nomMed, preMed, stkMed, 5, droga, false, desc));
                        System.out.println("¡Medicamento incorporado al inventario con exito!");
                        break;

                    case 2:
                        // ALTA DE PERFUMERÍA CON HERENCIA
                        System.out.print("Nombre Comercial: ");
                        String nomPerf = scanner.nextLine();
                        System.out.print("Precio de Venta Base: ");
                        double prePerf = scanner.nextDouble();
                        System.out.print("Stock Inicial: ");
                        int stkPerf = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Categoria de Perfumeria: ");
                        String catPerf = scanner.nextLine();
                        inventario.add(new ArticuloPerfumeria(inventario.size() + 1, "PERF-" + System.currentTimeMillis(), nomPerf, prePerf, stkPerf, 4, catPerf, "Unidad", 5.0));
                        System.out.println("¡Articulo de perfumeria incorporado con exito!");
                        break;

                    case 3:
                        // DEMOSTRACIÓN DE POLIMORFISMO AL LISTAR
                        System.out.println("\n--- CATALOGO DISPONIBLE DE PRODUCTOS ---");
                        for (Producto p : inventario) {
                            // Invoca dinámicamente a calcularPrecioFinal() según el tipo real del objeto
                            System.out.printf("[%d] %-28s | Base: $%8.2f | Final: $%8.2f | Stock: %d%n",
                                    p.getIdProducto(), p.getNombreComercial(), p.getPrecioVenta(), p.calcularPrecioFinal(), p.getStockActual());
                        }
                        break;

                    case 4:
                        // OPERACIÓN DE VENTA Y MANEJO DE EXCEPCIONES DE NEGOCIO
                        System.out.print("Ingrese el ID del producto a vender: ");
                        long idProd = scanner.nextLong();
                        Producto productoSeleccionado = null;
                        for (Producto p : inventario) {
                            if (p.getIdProducto() == idProd) {
                                productoSeleccionado = p;
                                break;
                            }
                        }

                        if (productoSeleccionado == null) {
                            throw new FarmaciaExcepcion("El producto con ID " + idProd + " no existe en el catalogo.");
                        }

                        System.out.print("Ingrese la cantidad a comprar: ");
                        int cantidad = scanner.nextInt();

                        if (cantidad <= 0) {
                            throw new FarmaciaExcepcion("La cantidad solicitada debe ser mayor a cero.");
                        }

                        String modalidad = "Entregado Inmediato";
                        if (productoSeleccionado.esVentaPorEncargo(cantidad)) {
                            System.out.println("AVISO: Stock fisico insuficiente (Disponible: " + productoSeleccionado.getStockActual() + ").");
                            System.out.println("Se registrara como VENTA POR ENCARGO a drogueria (saldo negativo).");
                            modalidad = "Pendiente de Entrega / Encargo";
                        }

                        productoSeleccionado.descontarStock(cantidad);
                        ventaActiva.agregarDetalle(new DetalleVenta(productoSeleccionado, cantidad, modalidad));
                        System.out.println("¡Producto agregado exitosamente al comprobante!");
                        break;

                    case 5:
                        // CONFIRMACIÓN DE VENTA Y REINICIO DE COMPROBANTE
                        if (!ventaActiva.hayItems()) {
                            System.out.println("Aviso: El carrito no contiene productos agregados.");
                        } else {
                            ventaActiva.mostrarDetalleVenta();
                            System.out.println("¡Venta confirmada exitosamente!");
                            ventaActiva = new Venta(ventaActiva.getIdVenta() + 1, "Efectivo"); // Nueva venta
                        }
                        break;

                    case 6:
                        // CIERRE LIMPIO DEL SISTEMA
                        System.out.println("Cerrando sesion de SIGEFAR. ¡Hasta pronto!");
                        scanner.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Opcion no valida. Seleccione un numero entre 1 y 6.");
                }

            } catch (InputMismatchException e) {
                // Control de error cuando el usuario ingresa letras en vez de números
                System.out.println("Error de entrada: Debe ingresar un numero entero valido para la opcion.");
                scanner.nextLine(); // Limpieza del valor incorrecto en el scanner
            } catch (FarmaciaExcepcion e) {
                // Captura y tratamiento de excepciones propias de negocio
                System.out.println("Excepcion de Negocio: " + e.getMessage());
            }
        }
    }
}
