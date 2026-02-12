package com.example.demo.controller;
import com.example.demo.model.Productos;
import com.example.demo.repository.ProductosRepository;


import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    private final ProductosRepository repository;

    public ProductosController(ProductosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Productos> listar() {
        return repository.findAll();
    }

    @PostMapping
    public void crear(@RequestBody Productos productos) {
        repository.save(productos);
    }
}