package com.example.demo.controller;
import com.example.demo.model.Pedidos;
import com.example.demo.repository.PedidosRepository;


import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/productos")
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
