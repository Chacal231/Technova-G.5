package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonObject; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    // 1. Buscamos en la BD
    Usuarios usuarioBD = repository.comprobarLogin(usuario.getEmail(), usuario.getPassword());

    if (usuarioBD == null) {
        // Creamos un JSON de error rápido
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("error", "Credenciales incorrectas");
        return ResponseEntity.status(401).body(errorJson.toString());
    }

    
    return ResponseEntity.ok(usuarioBD.toJsonObject().toString());
}
}