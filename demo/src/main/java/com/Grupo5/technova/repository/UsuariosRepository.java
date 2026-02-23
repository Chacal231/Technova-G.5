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

    // Este método llama a tu procedimiento 'sp_validar_login'
    public Usuarios comprobarLogin(String email, String password) {
        String sql = "{CALL sp_validar_login(?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // Pasamos los datos que vienen del controlador
            cs.setString(1, email);
            cs.setString(2, password);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    // Si el procedimiento devuelve una fila, creamos el usuario
                    return new Usuarios(
                        rs.getInt("id"),
                        rs.getString("email"),
                        null, // La contraseña no la devolvemos por seguridad
                        rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no hay usuario o hay error, devolvemos null
    }
}
