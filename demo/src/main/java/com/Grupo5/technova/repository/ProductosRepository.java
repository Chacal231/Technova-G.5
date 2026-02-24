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

    public ProductosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Productos> findAll() {
        List<Productos> lista = new ArrayList<>();
        String sql = "{CALL sp_productos_listar()}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

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

    public List<Productos> findByCategoria(String categoria) {
        List<Productos> lista = new ArrayList<>();
        String sql = "{CALL sp_productos_por_categoria(?)}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, categoria);
            
            try (ResultSet rs = cs.executeQuery()) {
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

    public void actualizarStock(int idProducto, int cantidad) {
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