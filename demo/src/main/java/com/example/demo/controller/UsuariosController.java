package com.example.demo.controller;
import com.example.demo.model.Usuarios;
import com.example.demo.repository.UsuariosRepository;
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