package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Productos;
import com.Grupo5.technova.repository.ProductosRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductosController {

    private final ProductosRepository repository;

    public ProductosController(ProductosRepository repository) {
        this.repository = repository;
    }

    // PUBLICO: Todos pueden ver productos
    @GetMapping
    public ResponseEntity<String> listarTodos() {
        List<Productos> lista = repository.findAll();
        String jsonFinal = convertirListaAJson(lista);
        return ResponseEntity.ok(jsonFinal);
    }

    // PUBLICO: Todos pueden ver productos por categoría
    @GetMapping("/{categoria}")
    public ResponseEntity<String> listarPorCategoria(@PathVariable String categoria) {
        List<Productos> lista = repository.findByCategoria(categoria);
        String jsonFinal = convertirListaAJson(lista);
        return ResponseEntity.ok(jsonFinal);
    }

    //  SOLO ADMIN: Crear producto
    @PostMapping
    public ResponseEntity<String> crearProducto(
            @RequestBody Productos producto,
            @RequestHeader(value = "user-role", required = false) String rol) {
        
        // Verificar si es ADMIN
        if (!"ADMIN".equals(rol)) {
            JsonObject error = new JsonObject();
            error.addProperty("error", "Acceso denegado. Se requiere rol ADMIN");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error.toString());
        }
        
        // Lógica para crear producto 
        boolean creado = repository.crear(producto);
        if (creado) {
            return ResponseEntity.status(HttpStatus.CREATED).body(producto.toJsonObject().toString());
        }
        
        JsonObject error = new JsonObject();
        error.addProperty("error", "Error al crear producto");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
    }

    // SOLO ADMIN: Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarProducto(
            @PathVariable int id,
            @RequestBody Productos producto,
            @RequestHeader(value = "user-role", required = false) String rol) {
        
        if (!"ADMIN".equals(rol)) {
            JsonObject error = new JsonObject();
            error.addProperty("error", "Acceso denegado. Se requiere rol ADMIN");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error.toString());
        }
        
        producto.setId(id);
        boolean actualizado = repository.actualizar(producto);
        if (actualizado) {
            return ResponseEntity.ok(producto.toJsonObject().toString());
        }
        
        JsonObject error = new JsonObject();
        error.addProperty("error", "Producto no encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.toString());
    }

    // SOLO ADMIN: Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(
            @PathVariable int id,
            @RequestHeader(value = "user-role", required = false) String rol) {
        
        if (!"ADMIN".equals(rol)) {
            JsonObject error = new JsonObject();
            error.addProperty("error", "Acceso denegado. Se requiere rol ADMIN");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error.toString());
        }
        
        boolean eliminado = repository.eliminar(id);
        if (eliminado) {
            JsonObject ok = new JsonObject();
            ok.addProperty("mensaje", "Producto eliminado correctamente");
            return ResponseEntity.ok(ok.toString());
        }
        
        JsonObject error = new JsonObject();
        error.addProperty("error", "Producto no encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.toString());
    }

    // Utilidad para convertir lista a JSON
    private String convertirListaAJson(List<Productos> productos) {
        JsonArray arrayJson = new JsonArray();
        for (Productos p : productos) {
            arrayJson.add(p.toJsonObject());
        }
        return arrayJson.toString();
    }
}