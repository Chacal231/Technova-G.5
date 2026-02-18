package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;

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

    @GetMapping
    public List<Usuarios> listar() {
        return repository.findAll();
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
