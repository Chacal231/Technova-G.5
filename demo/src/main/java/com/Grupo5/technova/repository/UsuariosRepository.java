package com.Grupo5.technova.repository;

import com.Grupo5.technova.model.Usuarios;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
public class UsuariosRepository {

    private final DataSource dataSource;

    // Inyectamos la conexión a la base de datos
    public UsuariosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Método para validar el acceso del usuario mediante el procedimiento almacenado
    public Usuarios comprobarLogin(String email, String password) {
        // Usamos el procedimiento sp_validar_login definido en SQL
        String sql = "{CALL sp_validar_login(?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // Asignamos el email y password a la consulta
            cs.setString(1, email);
            cs.setString(2, password);

            try (ResultSet rs = cs.executeQuery()) {
                // Si la base de datos encuentra coincidencia, creamos el objeto Usuario
                if (rs.next()) {
                    return new Usuarios(
                        rs.getInt("id"),
                        rs.getString("email"),
                        null, // La contraseña no se envía al frontend por seguridad
                        rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            // Imprime el error en consola si algo falla en la conexión
            e.printStackTrace();
        }
        // Si no hay usuario o las credenciales fallan, devuelve null
        return null; 
    }
}