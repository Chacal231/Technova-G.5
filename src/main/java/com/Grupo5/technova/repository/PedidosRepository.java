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

    // Inyectamos la conexión a la base de datos para gestionar los pedidos
    public PedidosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
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

    // Registra la cabecera de un nuevo pedido y devuelve su ID generado
    public int crearCabecera(int idUsuario, double total) {
        // El tercer parámetro es de salida (OUT) para capturar el ID del nuevo pedido
        String sql = "{CALL sp_crear_pedido(?, ?, ?)}";
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idUsuario);
            cs.setDouble(2, total);
            // Registramos el tipo de dato del parámetro de salida
            cs.registerOutParameter(3, Types.INTEGER); 
            
            cs.execute();
            // Retornamos el ID generado por la base de datos
            return cs.getInt(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Retorno de error controlado
        }
    }

    // Inserta una línea de detalle (producto y cantidad) vinculada a un pedido
    public void crearLinea(int idPedido, int idProducto, int cantidad, double precio) {
        // Ejecutamos el procedimiento para guardar cada ítem del carrito
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

    // Método para sincronizar el stock físico con la venta realizada
    public void actualizarStock(int idProducto, int cantidadComprada) {
        // Llama al procedimiento que resta las unidades vendidas del almacén
        String sql = "{CALL sp_actualizar_stock(?, ?)}";
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idProducto);
            cs.setInt(2, cantidadComprada);
            cs.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}