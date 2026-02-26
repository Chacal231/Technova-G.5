package com.Grupo5.technova.controller;

import com.Grupo5.technova.model.Lineas_Pedido;
import com.Grupo5.technova.model.Pedidos;
import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.PedidosRepository;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Endpoint para registrar un nuevo pedido validando la identidad del usuario
    @PostMapping
    public ResponseEntity<String> guardar(
            @RequestHeader("email") String email,
            @RequestHeader("password") String password,
            @RequestBody Pedidos pedidoRecibido) {

        // 1. Verificamos que las credenciales enviadas en las cabeceras sean válidas
        Usuarios usuarioBD = repositoryUsuarios.comprobarLogin(email, password);

        if (usuarioBD == null) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 2. Asignamos el ID del usuario autenticado al pedido
        pedidoRecibido.setId_usuario(usuarioBD.getId());
        JsonObject respuesta = new JsonObject();
        
        try {
            // 3. Creamos la cabecera del pedido y obtenemos su ID
            int nuevoId = repository.crearCabecera(
                    pedidoRecibido.getId_usuario(),
                    pedidoRecibido.getTotal_pedido());

            if (nuevoId != -1) {
                // 4. Si hay líneas de productos, las registramos una a una y actualizamos stock
                if (pedidoRecibido.getLineas() != null) {
                    for (Lineas_Pedido linea : pedidoRecibido.getLineas()) {
                        repository.crearLinea(
                                nuevoId,
                                linea.getId_producto(),
                                linea.getCantidad(),
                                linea.getPrecio_unitario());
                        
                        // Sincronizamos el stock del producto tras la compra
                        repository.actualizarStock(linea.getId_producto(), linea.getCantidad());
                    }
                }
                respuesta.addProperty("status", "ok");
                respuesta.addProperty("id_pedido", nuevoId);
                return ResponseEntity.ok(respuesta.toString());
            } else {
                respuesta.addProperty("status", "error");
                return ResponseEntity.status(421).body(respuesta.toString());
            }
        } catch (Exception e) {
            respuesta.addProperty("status", "error");
            return ResponseEntity.status(421).body(respuesta.toString());
        }
    }
}