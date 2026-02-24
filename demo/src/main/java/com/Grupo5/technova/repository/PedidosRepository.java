package com.Grupo5.technova.repository;

import com.Grupo5.technova.model.Pedidos;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PedidosRepository {
    private final DataSource dataSource;

    public PedidosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Pedidos> listarPedidos() {
        List<Pedidos> lista = new ArrayList<>();
        String sql = "{CALL sp_pedidos_listar()}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

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

    public int crearCabecera(int idUsuario, double total) {
        String sql = "{CALL sp_crear_pedido(?, ?, ?)}";
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idUsuario);
            cs.setDouble(2, total);
            cs.registerOutParameter(3, Types.INTEGER); 
            
            cs.execute();
            return cs.getInt(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void crearLinea(int idPedido, int idProducto, int cantidad, double precio) {
        String sql = "{CALL sp_crear_linea_pedido(?, ?, ?, ?)}";
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idPedido);
            cs.setInt(2, idProducto);
            cs.setInt(3, cantidad);
            cs.setDouble(4, precio);
            cs.execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void actualizarStock(int idProducto, int cantidadComprada) {
        String sql = "{CALL sp_actualizar_stock(?, ?)}";
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idProducto);
            cs.setInt(2, cantidadComprada);
            cs.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}