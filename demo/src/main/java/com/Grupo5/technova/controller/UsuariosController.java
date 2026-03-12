package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UsuariosController {
    
    private final UsuariosRepository repository;

    public UsuariosController(UsuariosRepository repository) {
        this.repository = repository;
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuarios usuario) {
        // Normalizar email (evitar espacios accidentales)
        String emailIntroducido = usuario.getEmail() != null
                ? usuario.getEmail().trim()
                : null;

        System.out.println("[LOGIN] Intento de login para email: '" + emailIntroducido + "'");

        // 1. Buscar usuario por email
        Usuarios usuarioConHash = repository.buscarPorEmail(emailIntroducido);

        // 2. Si el usuario no existe → error genérico
        if (usuarioConHash == null) {
            System.out.println("[LOGIN] Usuario no encontrado en BD");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 3. Verificar contraseña con soporte a contraseñas antiguas en texto plano
        String passwordAlmacenada = usuarioConHash.getPassword();
        if (passwordAlmacenada != null) {
            passwordAlmacenada = passwordAlmacenada.trim(); // por si viene con espacios
            usuarioConHash.setPassword(passwordAlmacenada);
            System.out.println("[LOGIN] Password almacenada (trim) length=" + passwordAlmacenada.length());
        }
        String passwordIntroducida = usuario.getPassword();

        boolean esHashBCrypt = passwordAlmacenada != null && passwordAlmacenada.startsWith("$2");
        boolean passwordCorrecta;

        if (esHashBCrypt) {
            System.out.println("[LOGIN] Detectado hash BCrypt en BD");
            // Caso normal: la BD ya almacena un hash BCrypt
            passwordCorrecta = BCrypt.checkpw(passwordIntroducida, passwordAlmacenada);
        } else {
            System.out.println("[LOGIN] Contraseña en BD tratada como texto plano");
            // Caso legado: contraseña almacenada en texto plano
            passwordCorrecta = passwordIntroducida != null && passwordIntroducida.equals(passwordAlmacenada);

            // Si coincide en texto plano, migramos automáticamente a BCrypt
            if (passwordCorrecta) {
                String nuevoHash = BCrypt.hashpw(passwordIntroducida, BCrypt.gensalt());
                repository.actualizarPasswordHash(usuarioConHash.getId(), nuevoHash);
                usuarioConHash.setPassword(nuevoHash);
            }
        }

        // 4. Si contraseña incorrecta → error
        if (!passwordCorrecta) {
            System.out.println("[LOGIN] Contraseña INCORRECTA para email: '" + emailIntroducido + "'");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 5. Llamar al procedimiento con el hash
        Usuarios usuarioValidado = repository.comprobarLogin(
            usuario.getEmail(), 
            usuarioConHash.getPassword()
        );

        // 6. Si el procedimiento falla (no debería) → error
        if (usuarioValidado == null) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Error en la validación");
            return ResponseEntity.status(500).body(errorJson.toString());
        }

        // 7. Todo bien → devolver datos del usuario
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("id", usuarioConHash.getId());
        responseJson.addProperty("email", usuarioConHash.getEmail());
        responseJson.addProperty("rol", usuarioConHash.getRol());
        
        return ResponseEntity.ok(responseJson.toString());
    }
}