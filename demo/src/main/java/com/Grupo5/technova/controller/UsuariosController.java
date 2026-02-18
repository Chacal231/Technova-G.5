package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonArray;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosRepository repository;

    public UsuariosController(UsuariosRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listar () {
        List<Usuarios> usuarios = repository.findAll();

        JsonArray array = new JsonArray();
        for (Usuarios u : usuarios) {
            array.add(u.toJsonObject());
        }

        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(array.toString());
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody Usuarios usuarios) {
        repository.save(usuarios);
        return ResponseEntity.status(201)
            .header("Access-Control-Allow-Origin", "*")
            .body("{\"mensaje\": \"Usuario creado correctamente\"}");
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Usuarios usuario) {

        boolean valido = repository.comprobarLogin(
            usuario.getEmail(),
            usuario.getPassword()
        );

        if (valido) {
            return ResponseEntity.ok(Map.of("status", "ok", "rol", "admin"));
        } 
        else {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales incorrectas"));
        }
    }

} 
fdfdff