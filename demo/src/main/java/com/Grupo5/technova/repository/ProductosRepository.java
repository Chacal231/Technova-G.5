package com.Grupo5.technova.repository;

import com.Grupo5.technova.model.Productos;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductosRepository {
    
    private final DataSource dataSource;

    // Inyectamos la conexión a la base de datos para manejar los productos
    public ProductosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Obtiene la lista completa de productos del catálogo
    public List<Productos> findAll() {
        List<Productos> lista = new ArrayList<>();
        // Llamamos al procedimiento almacenado para listar todo
        String sql = "{CALL sp_productos_listar()}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            // Recorremos los resultados y creamos los objetos Producto
            while (rs.next()) {
                lista.add(new Productos(
                    rs.getInt("id"), rs.getString("sku"), rs.getString("nombre"),
                    rs.getString("descripcion"), rs.getDouble("precio"),
                    rs.getInt("stock"), rs.getString("categoria"), rs.getString("imagen")
                ));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return lista;
    }

    // Obtiene productos filtrados por una categoría específica
    public List<Productos> findByCategoria(String categoria) {
        List<Productos> lista = new ArrayList<>();
        // Llamamos al procedimiento que filtra por el nombre de la categoría
        String sql = "{CALL sp_productos_por_categoria(?)}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, categoria);
            
            try (ResultSet rs = cs.executeQuery()) {
                // Mapeamos los resultados a la lista de productos
                while (rs.next()) {
                    lista.add(new Productos(
                        rs.getInt("id"), rs.getString("sku"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getDouble("precio"),
                        rs.getInt("stock"), rs.getString("categoria"), rs.getString("imagen")
                    ));
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return lista;
    }

    // Método para restar unidades del inventario tras una compra
    public void actualizarStock(int idProducto, int cantidad) {
        // Ejecutamos el procedimiento de actualización de stock
        String sql = "{CALL sp_actualizar_stock(?, ?)}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idProducto);
            cs.setInt(2, cantidad);
            cs.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}