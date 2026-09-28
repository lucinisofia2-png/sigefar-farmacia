/*
 */

package com.sigefar.gui;

import com.sigefar.dao.VentaDAO;
import com.sigefar.model.Producto;
import com.sigefar.model.DetalleVenta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaVenta extends JFrame {
    private JComboBox<Producto> comboProductos;
    private JTextField txtCantidad;
    private JTable tablaVenta;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private double totalVenta = 0.0;
    
    private VentaDAO ventaDAO = new VentaDAO();
    private List<DetalleVenta> listaDetalles = new ArrayList<>();

    public VentanaVenta() {
        setTitle("SIGEFAR - Sistema de Mostrador y Facturación");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Selección y Carga
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        comboProductos = new JComboBox<>();
        cargarProductosEnCombo();
        txtCantidad = new JTextField("1", 4);
        JButton btnAgregar = new JButton("Agregar al Carrito");

        panelSuperior.add(new JLabel("Producto:"));
        panelSuperior.add(comboProductos);
        panelSuperior.add(new JLabel("Cantidad:"));
        panelSuperior.add(txtCantidad);
        panelSuperior.add(btnAgregar);
        add(panelSuperior, BorderLayout.NORTH);

        // Centro: Tabla del Comprobante
        modeloTabla = new DefaultTableModel(new String[]{"Producto", "Cant.", "Precio Unit.", "Subtotal", "Modalidad / Entrega"}, 0);
        tablaVenta = new JTable(modeloTabla);
        add(new JScrollPane(tablaVenta), BorderLayout.CENTER);

        // Panel Inferior: Total y Botón Confirmar
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnConfirmar = new JButton("Confirmar y Cobrar Venta");
        btnConfirmar.setBackground(new Color(40, 167, 69));
        btnConfirmar.setForeground(Color.WHITE);

        panelInferior.add(lblTotal);
        panelInferior.add(btnConfirmar);
        add(panelInferior, BorderLayout.SOUTH);

        // Evento Agregar Producto
        btnAgregar.addActionListener(e -> agregarProducto());

        // Evento Confirmar Venta
        btnConfirmar.addActionListener(e -> confirmarVenta());
    }

    private void cargarProductosEnCombo() {
        comboProductos.removeAllItems();
        List<Producto> productos = ventaDAO.obtenerCatalogoActivo();
        for (Producto p : productos) {
            comboProductos.addItem(p);
        }
    }

    private void agregarProducto() {
        Producto p = (Producto) comboProductos.getSelectedItem();
        if (p == null) return;

        int cant;
        try {
            cant = Integer.parseInt(txtCantidad.getText().trim());
            if (cant <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String modalidad = "Entregado Inmediato";
        // Regla de Negocio: Venta por encargo ante stock insuficiente
        if (cant > p.getStockActual()) {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                "Stock físico insuficiente (Disponible: " + p.getStockActual() + " unidades).\n" +
                "¿Desea registrar como VENTA POR ENCARGO a droguería (saldo negativo)?",
                "Advertencia de Stock",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (opcion != JOptionPane.YES_OPTION) return;
            modalidad = "Pendiente de Entrega / Encargo";
        }

        double subtotal = cant * p.getPrecioVenta();
        totalVenta += subtotal;
        lblTotal.setText(String.format("Total: $%.2f", totalVenta));

        listaDetalles.add(new DetalleVenta(p.getIdProducto(), cant, p.getPrecioVenta(), modalidad));
        modeloTabla.addRow(new Object[]{p.getNombreComercial(), cant, "$" + p.getPrecioVenta(), "$" + subtotal, modalidad});
    }

    private void confirmarVenta() {
        if (listaDetalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El comprobante no tiene productos agregados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = ventaDAO.registrarVenta(totalVenta, "Efectivo", 1, listaDetalles);
        if (exito) {
            JOptionPane.showMessageDialog(
                this, 
                "¡Venta registrada con éxito!\nComprobante emitido.\nStock actualizado en MySQL y pedidos generados si hubo encargos.",
                "Transacción Exitosa",
                JOptionPane.INFORMATION_MESSAGE
            );
            // Limpiar formulario y recargar combo con el stock actualizado en MySQL
            modeloTabla.setRowCount(0);
            listaDetalles.clear();
            totalVenta = 0.0;
            lblTotal.setText("Total: $0.00");
            cargarProductosEnCombo();
        } else {
            JOptionPane.showMessageDialog(this, "Error al procesar la transacción en la base de datos.", "Error Transaccional", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaVenta().setVisible(true));
    }
}
