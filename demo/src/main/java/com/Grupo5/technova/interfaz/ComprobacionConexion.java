package com.Grupo5.technova.interfaz;

import javax.sql.DataSource;
import java.sql.Connection;
import org.springframework.stereotype.Component;

@Component
public class ComprobacionConexion {

    private final DataSource dataSource;

    public ComprobacionConexion(DataSource dataSource) {
        this.dataSource = dataSource;
        verificarConexion();
    }

    public void verificarConexion() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                System.out.println("✓ Conexión a la base de datos establecida correctamente");
            }
        } catch (Exception e) {
            System.out.println("✗ Error al conectar a la base de datos: " + e.getMessage());
        }
    }

}