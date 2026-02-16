package com.Grupo5.technova.controller;
import com.Grupo5.technova.model.Pedidos;
import com.Grupo5.technova.repository.PedidosRepository;


import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidosController {

    private final PedidosRepository repository;

    public PedidosController(PedidosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Pedidos> listar() {
        return repository.findAll();
    }

    @PostMapping
    public void crear(@RequestBody Pedidos pedidos) {
        repository.save(pedidos);
    }
}
