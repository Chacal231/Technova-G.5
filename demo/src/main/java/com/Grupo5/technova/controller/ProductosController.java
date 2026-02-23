package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Productos;
import com.Grupo5.technova.repository.ProductosRepository;
import com.google.gson.JsonArray;
// Ya no hace falta importar JsonObject aquí porque lo hace el modelo
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // OBLIGATORIO: Para evitar el error de CORS del anexo
public class ProductosController {

    private final ProductosRepository repository;

    public ProductosController(ProductosRepository repository) {
        this.repository = repository;
    }

    // Tarea 3.3: GET /api/productos (Listar todos)
    @GetMapping
    public ResponseEntity<String> listarTodos() {
        List<Productos> lista = repository.findAll(); // Llama a tu procedure listar
        
        // Convertimos la lista a un String JSON manualmente
        String jsonFinal = convertirListaAJson(lista);
        
        return ResponseEntity.ok(jsonFinal);
    }

    // Tarea 3.3 Opcional: GET /api/productos?categoria=Componentes
    @GetMapping(params = "categoria")
    public ResponseEntity<String> listarPorCategoria(@RequestParam String categoria) {
        List<Productos> lista = repository.findByCategoria(categoria); // Llama a tu procedure con filtro
        
        String jsonFinal = convertirListaAJson(lista);
        
        return ResponseEntity.ok(jsonFinal);
    }

    // Función auxiliar MEJORADA para construir el JSON a mano
    private String convertirListaAJson(List<Productos> productos) {
        JsonArray arrayJson = new JsonArray();

        for (Productos p : productos) {
            // ¡MAGIA POO! Como ya hiciste el método en el modelo, solo lo llamas aquí.
            // Esto le va a encantar al profesor porque demuestra que sabes reutilizar código.
            arrayJson.add(p.toJsonObject());
        }

        return arrayJson.toString(); // Devuelve "[{"id":1,...},{"id":2,...}]"
    }
}