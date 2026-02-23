package com.Grupo5.technova.repository;

import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
public class PedidosRepository {
    private final DataSource dataSource;

    public PedidosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Tarea 3.4: Crear la cabecera del pedido y obtener su ID
    public int crearPedido(int idUsuario, double total) {
        String sql = "{CALL sp_crear_pedido(?, ?, ?)}";
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idUsuario);
            cs.setDouble(2, total);
            cs.registerOutParameter(3, Types.INTEGER); // Para el p_nuevo_id (OUT)
            
            cs.execute();
            return cs.getInt(3); // Devolvemos el ID que nos da MySQL
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Tarea 3.4: Insertar cada línea del pedido
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
}