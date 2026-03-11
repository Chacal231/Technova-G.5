package com.Grupo5.technova.interfaz;

import javax.sql.DataSource;
import java.sql.Connection;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de validar la conectividad con la base de datos al iniciar la aplicación.
 */
@Component
public class ComprobacionConexion {

    private final DataSource dataSource;

    // Al inyectar el DataSource, ejecutamos automáticamente la verificación
    public ComprobacionConexion(DataSource dataSource) {
        this.dataSource = dataSource;
        verificarConexion();
    }

    // Método que intenta abrir una conexión física para confirmar que el servicio está activo
    public void verificarConexion() {
        try (Connection conn = dataSource.getConnection()) {
            // Comprobamos si la conexión es válida con un timeout de 2 segundos
            if (conn.isValid(2)) {
                System.out.println("✓ Conexión a la base de datos establecida correctamente");
            }
        } catch (Exception e) {
            // Notificamos por consola en caso de fallo en la configuración o el servidor SQL
            System.out.println("✗ Error al conectar a la base de datos: " + e.getMessage());
        }
    }

}