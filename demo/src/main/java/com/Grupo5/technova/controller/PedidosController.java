package com.Grupo5.technova.controller;
import com.Grupo5.technova.DTO.CheckoutRequest;
import com.Grupo5.technova.model.Pedidos;
import com.Grupo5.technova.repository.PedidosRepository;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
// Permite peticiones desde el Frontend (CORS)
@CrossOrigin(origins = "*")
public class PedidosController {

    private final PedidosRepository repository;
    private final UsuariosRepository repositoryUsuarios;

    // Inyectamos los repositorios necesarios para gestionar pedidos y validar usuarios
    public PedidosController(PedidosRepository repository, UsuariosRepository repositoryUsuarios) {
        this.repository = repository;
        this.repositoryUsuarios = repositoryUsuarios;
    }

    // Endpoint para obtener el listado global de pedidos
    @GetMapping
    public ResponseEntity<String> listar() {
        List<Pedidos> lista = repository.listarPedidos();
        JsonArray array = new JsonArray();
        
        // Convertimos cada pedido de la lista a formato JSON
        for (Pedidos p : lista) {
            array.add(p.toJsonObject());
        }
        return ResponseEntity.ok(array.toString());
    }

    // Endpoint checkout: valida datos, calcula total en servidor y descuenta stock
    @PostMapping
    public ResponseEntity<String> guardar(@RequestBody CheckoutRequest request) {
        JsonObject respuesta = new JsonObject();
        try {
            if (request == null || request.getId_usuario() == null) {
                respuesta.addProperty("status", "error");
                respuesta.addProperty("mensaje", "id_usuario es obligatorio");
                return ResponseEntity.badRequest().body(respuesta.toString());
            }
            if (!repositoryUsuarios.existePorId(request.getId_usuario())) {
                respuesta.addProperty("status", "error");
                respuesta.addProperty("mensaje", "El id_usuario no existe");
                return ResponseEntity.badRequest().body(respuesta.toString());
            }
            if (request.getProductos() == null || request.getProductos().isEmpty()) {
                respuesta.addProperty("status", "error");
                respuesta.addProperty("mensaje", "La lista de productos no puede estar vacía");
                return ResponseEntity.badRequest().body(respuesta.toString());
            }

            for (CheckoutRequest.ItemPedido item : request.getProductos()) {
                if (item == null || item.getId_producto() == null || item.getCantidad() == null) {
                    respuesta.addProperty("status", "error");
                    respuesta.addProperty("mensaje", "Formato de producto inválido");
                    return ResponseEntity.badRequest().body(respuesta.toString());
                }
                if (item.getCantidad() < 1) {
                    respuesta.addProperty("status", "error");
                    respuesta.addProperty("mensaje", "La cantidad debe ser un entero positivo");
                    return ResponseEntity.badRequest().body(respuesta.toString());
                }
            }

            PedidosRepository.CheckoutResult resultado =
                    repository.crearPedidoCompleto(request.getId_usuario(), request.getProductos());

            respuesta.addProperty("status", "ok");
            respuesta.addProperty("mensaje", "Pedido creado correctamente");
            respuesta.addProperty("id_pedido", resultado.getIdPedido());
            respuesta.addProperty("total_pedido", resultado.getTotalPedido());
            return ResponseEntity.ok(respuesta.toString());
        } catch (SQLException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            respuesta.addProperty("status", "error");
            if (msg.toLowerCase().contains("stock insuficiente")) {
                respuesta.addProperty("mensaje", "Stock insuficiente");
                return ResponseEntity.status(409).body(respuesta.toString());
            }
            if (msg.toLowerCase().contains("producto no encontrado")) {
                respuesta.addProperty("mensaje", "Producto no encontrado");
                return ResponseEntity.badRequest().body(respuesta.toString());
            }
            respuesta.addProperty("mensaje", "Error en base de datos");
            return ResponseEntity.status(500).body(respuesta.toString());
        } catch (Exception e) {
            respuesta.addProperty("status", "error");
            respuesta.addProperty("mensaje", "Error en base de datos");
            return ResponseEntity.status(500).body(respuesta.toString());
        }
    }
}