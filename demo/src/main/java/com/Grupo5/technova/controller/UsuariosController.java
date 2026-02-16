package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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

    @PostMapping
    public void crear(@RequestBody Usuarios usuarios) {
        repository.save(usuarios);
    }
}