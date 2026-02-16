package com.Grupo5.technova.model;

import java.time.LocalDateTime;

public class Pedidos {
    private Integer id;
    private Integer id_usuario;
    private LocalDateTime fecha;   
    private double total_pedido;
    private String categoria;
    public enum categoria{
        Pendiente, Enviado, Entregado, Cancelado
    }
    public Pedidos (){}
        public Pedidos (Integer id, Integer id_usuario, LocalDateTime fecha, double total_pedido, String categoria){
            this.id = id;
            this.id_usuario = id_usuario;
            this.fecha = fecha;
            this.total_pedido = total_pedido;
            this.categoria = categoria;
        }
        public Integer getId() {return id; }
        public Integer getId_usuario () { return id_usuario; }
        public LocalDateTime getFecha () { return fecha; }
        public double getTotal_pedido () { return total_pedido; }
        public String getCategoria () { return categoria;} 
}
