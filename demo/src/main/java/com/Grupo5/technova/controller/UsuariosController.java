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


    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Usuarios usuario) {
    Usuarios usuarioBD = repository.comprobarLogin(usuario.getEmail(),usuario.getPassword());

    if (usuarioBD == null) {
        return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales incorrectas"));
    }

    return ResponseEntity.ok(
            Map.of("status", "ok","rol", usuarioBD.getRol()));
}

}



