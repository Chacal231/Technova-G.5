package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Productos;
import com.Grupo5.technova.repository.ProductosRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    private final ProductosRepository repository;

    public ProductosController(ProductosRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = "application/json")
    

    @PostMapping
    public void crear(@RequestBody Productos productos) {
        repository.save(productos);
    }
}