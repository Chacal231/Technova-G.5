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

    // ============================================
    // MÉTODOS EXISTENTES (los dejas igual)
    // ============================================
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

    // ============================================
    // NUEVOS MÉTODOS CRUD (SOLO ADMIN)
    // ============================================

    // 1. CREAR producto
    public boolean crear(Productos producto) {
        String sql = "{CALL sp_producto_crear(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, producto.getSku());
            cs.setString(2, producto.getNombre());
            cs.setString(3, producto.getDescripcion());
            cs.setDouble(4, producto.getPrecio());
            cs.setInt(5, producto.getStock());
            cs.setString(6, producto.getCategoria());
            cs.setString(7, producto.getImagen());

            int filasAfectadas = cs.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. ACTUALIZAR producto
    public boolean actualizar(Productos producto) {
        String sql = "{CALL sp_producto_actualizar(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, producto.getId());
            cs.setString(2, producto.getSku());
            cs.setString(3, producto.getNombre());
            cs.setString(4, producto.getDescripcion());
            cs.setDouble(5, producto.getPrecio());
            cs.setInt(6, producto.getStock());
            cs.setString(7, producto.getCategoria());
            cs.setString(8, producto.getImagen());

            int filasAfectadas = cs.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. ELIMINAR producto
    public boolean eliminar(int id) {
        String sql = "{CALL sp_producto_eliminar(?)}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            int filasAfectadas = cs.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. BUSCAR por ID (útil para validar)
    public Productos buscarPorId(int id) {
        String sql = "{CALL sp_producto_por_id(?)}";

        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new Productos(
                        rs.getInt("id"), rs.getString("sku"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getDouble("precio"),
                        rs.getInt("stock"), rs.getString("categoria"), rs.getString("imagen")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}