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

    // Método 1: Buscar usuario por email (SOLO email, devuelve el hash)
    public Usuarios buscarPorEmail(String email) {
        String sql = "{CALL sp_obtener_usuario_por_email(?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, email);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String nombre = obtenerNombreOpcional(rs);
                    return new Usuarios(
                        rs.getInt("id"),
                        rs.getString("email"),
                        nombre,
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
                    String nombre = obtenerNombreOpcional(rs);
                    return new Usuarios(
                        rs.getInt("id"),
                        rs.getString("email"),
                        nombre,
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

    // Registrar un nuevo usuario con rol CLIENTE y devolver su ID generado
    public int registrar(String nombre, String email, String passwordHash) {
        String sqlConNombre = "INSERT INTO Usuarios (nombre, email, password, rol) VALUES (?, ?, ?, 'CLIENTE')";
        String sqlSinNombre = "INSERT INTO Usuarios (email, password, rol) VALUES (?, ?, 'CLIENTE')";

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlConNombre, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.setString(2, email);
                ps.setString(3, passwordHash);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            } catch (SQLException ex) {
                // Compatibilidad con esquemas antiguos sin columna "nombre"
                try (PreparedStatement ps = conn.prepareStatement(sqlSinNombre, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, email);
                    ps.setString(2, passwordHash);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean existePorId(int idUsuario) {
        String sql = "SELECT 1 FROM Usuarios WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Usuarios> listarUsuarios() {
        List<Usuarios> usuarios = new ArrayList<>();
        String sqlConNombre = "SELECT id, nombre, email, rol FROM Usuarios ORDER BY id";
        String sqlSinNombre = "SELECT id, email, rol FROM Usuarios ORDER BY id";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlConNombre);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(new Usuarios(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("nombre"),
                            null,
                            rs.getString("rol")
                    ));
                }
            } catch (SQLException ex) {
                // Compatibilidad con esquemas antiguos sin columna "nombre".
                try (PreparedStatement ps = conn.prepareStatement(sqlSinNombre);
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String email = rs.getString("email");
                        usuarios.add(new Usuarios(
                                rs.getInt("id"),
                                email,
                                nombreDesdeEmail(email),
                                null,
                                rs.getString("rol")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public boolean existeOtroUsuarioConEmail(int idUsuario, String email) {
        String sql = "SELECT 1 FROM Usuarios WHERE email = ? AND id <> ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarUsuario(int id, String nombre, String email, String rol) {
        String sql = "UPDATE Usuarios SET nombre = ?, email = ?, rol = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, rol);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM Usuarios WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String obtenerNombreOpcional(ResultSet rs) {
        try {
            String nombre = rs.getString("nombre");
            if (nombre != null && !nombre.trim().isEmpty()) {
                return nombre.trim();
            }
        } catch (SQLException ignored) {
            // La columna no existe en esquemas antiguos.
        }
        return null;
    }

    private String nombreDesdeEmail(String email) {
        if (email == null) return "";
        int at = email.indexOf('@');
        if (at <= 0) return email;
        return email.substring(0, at);
    }
}
