package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonObject; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
// Permite peticiones desde el Frontend (CORS)
@CrossOrigin(origins = "*") 
public class UsuariosController {

    private final UsuariosRepository repository;

    // Inyectamos el repositorio de usuarios para las operaciones de validación
    public UsuariosController(UsuariosRepository repository) {
        this.repository = repository;
    }

    // Endpoint para gestionar el acceso de usuarios al sistema
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuarios usuario) {
        
        // 1. Validamos las credenciales contra la base de datos
        Usuarios usuarioBD = repository.comprobarLogin(usuario.getEmail(), usuario.getPassword());

        // 2. Si el usuario no existe o la contraseña es errónea, devolvemos error 401
        if (usuarioBD == null) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 3. Si las credenciales son correctas, devolvemos el objeto usuario en formato JSON
        return ResponseEntity.ok(usuarioBD.toJsonObject().toString());
    }
}
