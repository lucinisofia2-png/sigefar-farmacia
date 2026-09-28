# SIGEFAR - Sistema Integral de Gestión y Facturación para Farmacia

**Materia:** Seminario de Práctica Profesional  
**Carrera:** Licenciatura en Informática - Universidad Siglo 21  
**Estudiante:** Lucini Altamirano Sofía Inés  
**Profesor Titular Disciplinar:** Pablo Alejandro Virgolini  
**Entrega:** Segundo Trabajo Práctico (AP2)  

---

## 📌 Descripción del Proyecto
SIGEFAR es un prototipo operacional de software desarrollado bajo el **Proceso Unificado de Desarrollo (PUD)** para una farmacia. El sistema permite la consulta de productos de mostrador, el registro de ventas con actualización atómica de existencias (ACID), soporte para **ventas por encargo ante saldo negativo** y la generación automática de pedidos de reposición hacia droguerías.

---

## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Java SE (JDK 21)
* **Interfaz Gráfica:** Java Swing
* **Base de Datos:** MySQL Server 8.0 (Motor transaccional InnoDB)
* **Conector:** MySQL Connector/J 8.3.0 (JDBC Tipo 4)
* **Gestor de Construcción:** Apache Maven
* **IDE:** Apache NetBeans IDE 25

---

## 📁 Estructura del Código Fuente
```text
src/main/java/
├── com.sigefar.gui/
│   └── VentanaVenta.java          # Interfaz gráfica de usuario y punto de entrada (main)
├── com.sigefar.dao/
│   ├── ConexionBD.java            # Conexión JDBC a MySQL bajo patrón Singleton
│   └── VentaDAO.java              # Persistencia relacional y transacciones atómicas
└── com.sigefar.model/
    ├── Producto.java              # Entidad del catálogo e inventario
    └── DetalleVenta.java          # Entidad de línea de comprobante y estado de entrega
