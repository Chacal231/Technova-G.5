package com.Grupo5.technova.repository;

import com.Grupo5.technova.DTO.CheckoutRequest;
import com.Grupo5.technova.model.Pedidos;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PedidosRepository {
    
    private final DataSource dataSource;

    // Inyectamos la conexión a la base de datos para gestionar los pedidos
    public PedidosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static class CheckoutResult {
        private final int idPedido;
        private final double totalPedido;

        public CheckoutResult(int idPedido, double totalPedido) {
            this.idPedido = idPedido;
            this.totalPedido = totalPedido;
        }

        public int getIdPedido() {
            return idPedido;
        }

        public double getTotalPedido() {
            return totalPedido;
        }
    }

    // Obtiene el historial completo de pedidos registrados
    public List<Pedidos> listarPedidos() {
        List<Pedidos> lista = new ArrayList<>();
        // Llamamos al procedimiento almacenado para obtener la lista de pedidos
        String sql = "{CALL sp_pedidos_listar()}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            // Mapeamos cada fila de la base de datos al modelo Pedidos
            while (rs.next()) {
                lista.add(new Pedidos(
                    rs.getInt("id"),
                    rs.getInt("id_usuario"), 
                    rs.getString("fecha"),
                    rs.getDouble("total_pedido"), 
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public CheckoutResult crearPedidoCompleto(int idUsuario, List<CheckoutRequest.ItemPedido> productos) throws SQLException {
        String sqlProducto = "SELECT precio, stock FROM Productos WHERE id = ? FOR UPDATE";
        String sqlPedido = "INSERT INTO Pedidos (id_usuario, fecha, total_pedido, estado) VALUES (?, NOW(), ?, 'Pendiente')";
        String sqlLinea = "INSERT INTO Lineas_Pedido (id, id_pedido, id_producto, cantidad, precio_unitario_momento) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE Productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try {
                double total = 0.0;
                List<Double> precios = new ArrayList<>();
                List<Integer> cantidades = new ArrayList<>();
                List<Integer> idsProducto = new ArrayList<>();

                for (CheckoutRequest.ItemPedido item : productos) {
                    try (PreparedStatement psProducto = con.prepareStatement(sqlProducto)) {
                        psProducto.setInt(1, item.getId_producto());
                        try (ResultSet rs = psProducto.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Producto no encontrado");
                            }
                            int stock = rs.getInt("stock");
                            if (item.getCantidad() > stock) {
                                throw new SQLException("Stock insuficiente");
                            }
                            double precio = rs.getDouble("precio");
                            total += precio * item.getCantidad();
                            precios.add(precio);
                            cantidades.add(item.getCantidad());
                            idsProducto.add(item.getId_producto());
                        }
                    }
                }

                int idPedido;
                try (PreparedStatement psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    psPedido.setInt(1, idUsuario);
                    psPedido.setDouble(2, total);
                    psPedido.executeUpdate();
                    try (ResultSet keys = psPedido.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se pudo crear el pedido");
                        }
                        idPedido = keys.getInt(1);
                    }
                }

                for (int i = 0; i < idsProducto.size(); i++) {
                    try (PreparedStatement psLinea = con.prepareStatement(sqlLinea)) {
                        psLinea.setInt(1, i + 1);
                        psLinea.setInt(2, idPedido);
                        psLinea.setInt(3, idsProducto.get(i));
                        psLinea.setInt(4, cantidades.get(i));
                        psLinea.setDouble(5, precios.get(i));
                        psLinea.executeUpdate();
                    }

                    try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                        psStock.setInt(1, cantidades.get(i));
                        psStock.setInt(2, idsProducto.get(i));
                        psStock.setInt(3, cantidades.get(i));
                        int filas = psStock.executeUpdate();
                        if (filas == 0) {
                            throw new SQLException("Stock insuficiente");
                        }
                    }
                }

                con.commit();
                return new CheckoutResult(idPedido, total);
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean actualizarEstado(int idPedido, String estado) {
        String sql = "UPDATE Pedidos SET estado = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarPedido(int idPedido) {
        String sqlLineas = "DELETE FROM Lineas_Pedido WHERE id_pedido = ?";
        String sqlPedido = "DELETE FROM Pedidos WHERE id = ?";
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psLineas = con.prepareStatement(sqlLineas);
                 PreparedStatement psPedido = con.prepareStatement(sqlPedido)) {
                psLineas.setInt(1, idPedido);
                psLineas.executeUpdate();

                psPedido.setInt(1, idPedido);
                boolean eliminado = psPedido.executeUpdate() > 0;
                con.commit();
                return eliminado;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}