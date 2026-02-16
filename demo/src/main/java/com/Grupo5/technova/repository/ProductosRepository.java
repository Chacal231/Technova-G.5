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
        List<Productos> productos = new ArrayList<>();

        String sql = "SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen FROM productos";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Productos p = new Productos(
                        rs.getInt("id"),
                        rs.getString("sku"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("categoria"),
                        rs.getString("imagen")
                );
                productos.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return productos;
    }

    public void save(Productos productos) {
        String sql = "INSERT INTO productos (sku, nombre, descripcion, precio, stock, categoria, imagen) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, productos.getSku());
            ps.setString(2, productos.getNombre());
            ps.setString(3, productos.getDescripcion());
            ps.setDouble(4, productos.getPrecio());
            ps.setInt(5, productos.getStock());
            ps.setString(6, productos.getCategoria());
            ps.setString(7, productos.getImagen());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
