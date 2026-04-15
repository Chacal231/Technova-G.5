package com.Grupo5.technova.controller;
import com.Grupo5.technova.DTO.CheckoutRequest;
import com.Grupo5.technova.model.Pedidos;
import com.Grupo5.technova.repository.PedidosRepository;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<String> listar(@RequestHeader(value = "user-role", required = false) String rol) {
        if (!tieneRol(rol, "ADMIN", "OFICINA")) {
            return respuestaError(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN u OFICINA");
        }
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
            if (request.getId_usuario() < 1) {
                respuesta.addProperty("status", "error");
                respuesta.addProperty("mensaje", "id_usuario debe ser un entero positivo");
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
                if (item.getId_producto() < 1) {
                    respuesta.addProperty("status", "error");
                    respuesta.addProperty("mensaje", "id_producto debe ser un entero positivo");
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

    @PutMapping("/{id}/estado")
    public ResponseEntity<String> actualizarEstado(
            @PathVariable int id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "user-role", required = false) String rol) {
        if (!tieneRol(rol, "ADMIN", "OFICINA")) {
            return respuestaError(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN u OFICINA");
        }
        if (body == null || !body.containsKey("estado")) {
            return respuestaError(HttpStatus.BAD_REQUEST, "El campo estado es obligatorio");
        }
        String estado = normalizarEstado(body.get("estado"));
        if (estado == null) {
            return respuestaError(HttpStatus.BAD_REQUEST, "Estado inválido");
        }

        boolean actualizado = repository.actualizarEstado(id, estado);
        if (!actualizado) {
            return respuestaError(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }

        JsonObject ok = new JsonObject();
        ok.addProperty("mensaje", "Estado actualizado correctamente");
        ok.addProperty("id", id);
        ok.addProperty("estado", estado);
        return ResponseEntity.ok(ok.toString());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable int id,
            @RequestHeader(value = "user-role", required = false) String rol) {
        if (!tieneRol(rol, "ADMIN", "OFICINA")) {
            return respuestaError(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN u OFICINA");
        }
        boolean eliminado = repository.eliminarPedido(id);
        if (!eliminado) {
            return respuestaError(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        JsonObject ok = new JsonObject();
        ok.addProperty("mensaje", "Pedido eliminado correctamente");
        return ResponseEntity.ok(ok.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> manejarBodyInvalido(HttpMessageNotReadableException ex) {
        JsonObject respuesta = new JsonObject();
        respuesta.addProperty("status", "error");
        respuesta.addProperty("mensaje", "JSON inválido o tipos de datos incorrectos");
        return ResponseEntity.badRequest().body(respuesta.toString());
    }

    private String normalizarEstado(String estado) {
        if (estado == null) return null;
        if ("Pendiente".equalsIgnoreCase(estado)) return "Pendiente";
        if ("Enviado".equalsIgnoreCase(estado)) return "Enviado";
        if ("Entregado".equalsIgnoreCase(estado)) return "Entregado";
        if ("Cancelado".equalsIgnoreCase(estado)) return "Cancelado";
        return null;
    }

    private boolean tieneRol(String rolRecibido, String... permitidos) {
        if (rolRecibido == null) return false;
        for (String permitido : permitidos) {
            if (permitido.equalsIgnoreCase(rolRecibido.trim())) {
                return true;
            }
        }
        return false;
    }

    private ResponseEntity<String> respuestaError(HttpStatus status, String mensaje) {
        JsonObject error = new JsonObject();
        error.addProperty("error", mensaje);
        return ResponseEntity.status(status).body(error.toString());
    }
}