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

    //Método para buscar un usuario por email
    public Usuarios buscarPorEmail(String email, String passwordHash) {
        String sql = "{CALL sp_buscar_usuario_por_email(?)}";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashEnBD = rs.getString("password_hash");
                    // Verificamos el hash de la contraseña
                    if (BCrypt.checkpw(passwordHash, hashEnBD)) {
                        return new Usuarios(
                            rs.getInt("id"),
                            rs.getString("email"),
                            null,
                            rs.getString("rol")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

    //  Método para validar el login de un usuario utilizando el procedimiento almacenado sp_validar_login
    public Usuarios comprobarLogin(String email, String passwordHash) {
        String sql = "{CALL sp_validar_login(?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, email);
            cs.setString(2, passwordHash); // Aquí va el HASH

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