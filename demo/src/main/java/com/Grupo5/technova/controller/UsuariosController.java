package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonObject; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt; // Importamos la librería para el hashing de contraseñas

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

    // Login actualizado
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuarios usuario) {
        
        // 1. Validamos las credenciales contra la base de datos
        Usuarios usuarioConHash = repository.buscarPorEmail(usuario.getEmail(), usuario.getPassword());

        // 2. Si el usuario no existe o la contraseña es errónea, devolvemos error 401
        if (usuarioConHash == null) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 3. Si las credenciales son correctas, devolvemos el objeto usuario en formato JSON
        return ResponseEntity.ok(usuarioBD.toJsonObject().toString());
    }
}
