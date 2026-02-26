package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Productos;
import com.Grupo5.technova.repository.ProductosRepository;
import com.google.gson.JsonArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
// Permite peticiones desde el Frontend (CORS)
@CrossOrigin(origins = "*") 
public class ProductosController {

    private final ProductosRepository repository;

    // Inyectamos el repositorio para gestionar la lógica de datos de productos
    public ProductosController(ProductosRepository repository) {
        this.repository = repository;
    }

    // Endpoint para obtener todos los productos del catálogo
    @GetMapping
    public ResponseEntity<String> listarTodos() {
        List<Productos> lista = repository.findAll(); 
        
        // Convertimos la lista de objetos a una cadena JSON
        String jsonFinal = convertirListaAJson(lista);
        
        return ResponseEntity.ok(jsonFinal);
    }

    // Endpoint para filtrar productos según su categoría (ej: /api/productos/Componentes)
    @GetMapping("/{categoria}")
    public ResponseEntity<String> listarPorCategoria(@PathVariable String categoria) {
        List<Productos> lista = repository.findByCategoria(categoria); 
        
        String jsonFinal = convertirListaAJson(lista);
        
        return ResponseEntity.ok(jsonFinal);
    }

    /**
     * Utilidad interna para transformar la lista de modelos en un formato JSON Array.
     * Recorre cada producto y utiliza su método toJsonObject definido en el modelo.
     */
    private String convertirListaAJson(List<Productos> productos) {
        JsonArray arrayJson = new JsonArray();

        for (Productos p : productos) {
            arrayJson.add(p.toJsonObject());
        }

        return arrayJson.toString(); 
    }
}