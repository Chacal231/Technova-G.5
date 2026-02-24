package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Productos;
import com.Grupo5.technova.repository.ProductosRepository;
import com.google.gson.JsonArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") 
public class ProductosController {

    private final ProductosRepository repository;

    public ProductosController(ProductosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<String> listarTodos() {
        List<Productos> lista = repository.findAll(); 
        
        String jsonFinal = convertirListaAJson(lista);
        
        return ResponseEntity.ok(jsonFinal);
    }

    @GetMapping(params = "categoria")
    public ResponseEntity<String> listarPorCategoria(@RequestParam String categoria) {
        List<Productos> lista = repository.findByCategoria(categoria); 
        
        String jsonFinal = convertirListaAJson(lista);
        
        return ResponseEntity.ok(jsonFinal);
    }

    private String convertirListaAJson(List<Productos> productos) {
        JsonArray arrayJson = new JsonArray();

        for (Productos p : productos) {
            
            arrayJson.add(p.toJsonObject());
        }

        return arrayJson.toString(); 
    }
}