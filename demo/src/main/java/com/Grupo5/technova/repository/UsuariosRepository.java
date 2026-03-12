package com.Grupo5.technova.repository;
import com.Grupo5.technova.model.Usuarios;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
public class UsuariosRepository {

    private final DataSource dataSource;

    public UsuariosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Método 1: Buscar usuario por email (SOLO email, devuelve el hash)
    public Usuarios buscarPorEmail(String email) {
        String sql = "{CALL sp_obtener_usuario_por_email(?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, email);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new Usuarios(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"), // Devolvemos el hash para verificarlo en el controlador
                        rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método 2: Validar login con procedimiento almacenado
    public Usuarios comprobarLogin(String email, String passwordHash) {
        String sql = "{CALL sp_validar_login(?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, email);
            cs.setString(2, passwordHash);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new Usuarios(
                        rs.getInt("id"),
                        rs.getString("email"),
                        null,
                        rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método 3: Actualizar el hash de la contraseña (migración de texto plano a BCrypt)
    public void actualizarPasswordHash(int id, String nuevoHash) {
        String sql = "UPDATE Usuarios SET password = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}