/*
 */

package com.sigefar.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administrador de conexión a la base de datos MySQL.
 * 
 * PATRÓN DE DISEÑO: SINGLETON
 * - Constructor privado para impedir instanciación directa.
 * - Garantiza una única conexión compartida para optimizar recursos del sistema.
 */
public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;
    
    // Parámetros de conexión a MySQL local
    private static final String URL = "jdbc:mysql://localhost:3306/sigefar_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "root"; 

    private ConexionBD() {
        try {
            // Carga explícita del driver JDBC Tipo 4
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión a MySQL: " + e.getMessage());
        }
    }
    
    /**
     * Punto de acceso global y sincronizado a la instancia única.
     */
    public static synchronized ConexionBD getInstancia() {
        try {
            if (instancia == null || instancia.getConexion().isClosed()) {
                instancia = new ConexionBD();
            }
        } catch (SQLException e) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
}
