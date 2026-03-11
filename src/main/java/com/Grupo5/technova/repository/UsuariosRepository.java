package com.Grupo5.technova.repository;
import com.Grupo5.technova.model.Usuarios;
import org.mindrot.jbcrypt.BCrypt;
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
}