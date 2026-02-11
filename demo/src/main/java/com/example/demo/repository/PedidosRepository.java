package com.example.demo.repository;

import com.example.demo.model.Pedidos;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PedidosRepository {
    private final DataSource dataSource;

    public PedidosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Pedidos> findAll() {
        List<Pedidos> pedidos = new ArrayList<>();

        String sql = "SELECT id, id_usuario, fecha, total_pedido, categoria FROM pedidos";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedidos p = new Pedidos(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getObject("fecha", LocalDateTime.class),
                        rs.getDouble("total_pedido"),
                        rs.getString("categoria")
                );
                pedidos.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pedidos;
    }

    public void save(Pedidos pedidos) {
        String sql = "INSERT INTO pedidos (id_usuario, fecha, total_pedido, categoria) VALUES (?, ?, ?, ?)";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pedidos.getId_usuario());
            ps.setObject(2, pedidos.getFecha());
            ps.setDouble(3, pedidos.getTotal_pedido());
            ps.setString(4, pedidos.getCategoria());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
