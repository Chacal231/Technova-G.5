package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Productos;
import com.Grupo5.technova.repository.ProductosRepository;
import com.google.gson.JsonArray;

import org.springframework.http.MediaType;
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
    public ResponseEntity<String> listar () {
        List<Productos> productos = repository.findAll();

        JsonArray array = new JsonArray();
        for (Productos p : productos) {
            array.add(p.toJsonObject());
        }

        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(array.toString());
    }

    @PostMapping
    public void crear(@RequestBody Productos productos) {
        repository.save(productos);
    }
}