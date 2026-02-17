package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonArray;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosRepository repository;

    public UsuariosController(UsuariosRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = "application/json")
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
    public void crear(@RequestBody Usuarios usuarios) {
        repository.save(usuarios);
    }
}