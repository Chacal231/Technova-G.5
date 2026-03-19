package com.Grupo5.technova.DTO;

import java.util.List;

public class CheckoutRequest {
    private Integer id_usuario;
    private List<ItemPedido> productos;

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public List<ItemPedido> getProductos() {
        return productos;
    }

    public void setProductos(List<ItemPedido> productos) {
        this.productos = productos;
    }

    public static class ItemPedido {
        private Integer id_producto;
        private Integer cantidad;

        public Integer getId_producto() {
            return id_producto;
        }

        public void setId_producto(Integer id_producto) {
            this.id_producto = id_producto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
