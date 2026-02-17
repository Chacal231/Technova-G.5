package com.Grupo5.technova.repository;

import com.Grupo5.technova.model.Usuarios;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuariosRepository {
    private final DataSource dataSource;

    public UsuariosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Usuarios> findAll() {
        List<Usuarios> usuarios = new ArrayList<>();

        String sql = "SELECT id, email, password, rol FROM usuarios";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuarios u = new Usuarios(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        //Usuarios.Rol.valueOf(rs.getString("rol").replace(" ", "_"))
                        rs.getString("rol")
                );
                usuarios.add(u);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return usuarios;
    }

    public void save(Usuarios usuarios) {
        String sql = "INSERT INTO usuarios (email, password, rol) VALUES (?, ?, ?)";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuarios.getEmail());
            ps.setString(2, usuarios.getPassword());
           // ps.setString(3, usuarios.getRol().name().replace("_", " "));
            ps.setString(3, usuarios.getRol());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    } 
    public boolean comprobarLogin(String email, String password) {

    String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        return rs.next(); // si encuentra usuario devuelve true

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
}