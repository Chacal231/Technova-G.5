package com.Grupo5.technova.DTO;

import java.util.List;

/**
 * DTO que representa el cuerpo de la petición de checkout.
 * Contiene el identificador del usuario y la lista de productos
 * con sus cantidades para crear un nuevo pedido.
 */
public class CheckoutRequest {

    /** Identificador del usuario que realiza la compra. */
    private Integer id_usuario;

    /** Lista de productos e importes solicitados en el pedido. */
    private List<ItemPedido> productos;

    /**
     * Obtiene el identificador del usuario.
     * @return ID del usuario
     */
    public Integer getId_usuario() {
        return id_usuario;
    }

    /**
     * Establece el identificador del usuario.
     * @param id_usuario ID del usuario que realiza la compra
     */
    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    /**
     * Obtiene la lista de productos del pedido.
     * @return Lista de {@link ItemPedido}
     */
    public List<ItemPedido> getProductos() {
        return productos;
    }

    /**
     * Establece la lista de productos del pedido.
     * @param productos Lista de items con id_producto y cantidad
     */
    public void setProductos(List<ItemPedido> productos) {
        this.productos = productos;
    }

    /**
     * Clase interna que representa cada línea del pedido:
     * un producto concreto y la cantidad solicitada.
     */
    public static class ItemPedido {

        /** Identificador del producto solicitado. */
        private Integer id_producto;

        /** Número de unidades solicitadas del producto. */
        private Integer cantidad;

        /**
         * Obtiene el identificador del producto.
         * @return ID del producto
         */
        public Integer getId_producto() {
            return id_producto;
        }

        /**
         * Establece el identificador del producto.
         * @param id_producto ID del producto
         */
        public void setId_producto(Integer id_producto) {
            this.id_producto = id_producto;
        }

        /**
         * Obtiene la cantidad solicitada.
         * @return Número de unidades
         */
        public Integer getCantidad() {
            return cantidad;
        }

        /**
         * Establece la cantidad solicitada.
         * @param cantidad Número de unidades a comprar
         */
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}