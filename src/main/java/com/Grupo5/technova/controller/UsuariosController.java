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
        
        // 1. Buscar usuario por email
        Usuarios usuarioConHash = repository.buscarPorEmail(usuario.getEmail());

        // 2. Si el usuario no existe → error
        if (usuarioConHash == null) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 3. Verificar contraseña con BCrypt
        boolean passwordCorrecta = BCrypt.checkpw(
            usuario.getPassword(), 
            usuarioConHash.getPassword()
        );

        // 4. Si contraseña incorrecta → error
        if (!passwordCorrecta) {
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