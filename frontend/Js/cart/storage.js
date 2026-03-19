/**
 * PERSISTENCIA DEL CARRITO
 * ========================
 * Guarda y restaura el carrito usando localStorage.
 * Los datos persisten entre recargas del navegador.
 */

import { cart } from './cart.js';
import { renderCart } from './render.js';
import { safeImageUrl } from '../utils/formatters.js';

export function saveCart() {
  try { 
    localStorage.setItem('tn_cart', JSON.stringify(cart)); 
  } catch (_) {}
}

export function restoreCart() {
  try {
    const s = localStorage.getItem('tn_cart');
    if (s) {
      const restoredCart = JSON.parse(s);
      const cleanCart = Array.isArray(restoredCart)
        ? restoredCart
            .filter(item => item && item.id != null)
            .map(item => ({
              ...item,
              nombre: String(item.nombre ?? 'Producto'),
              categoria: String(item.categoria ?? 'General'),
              precio: Number(item.precio) >= 0 ? Number(item.precio) : 0,
              qty: Number.isInteger(Number(item.qty)) && Number(item.qty) > 0 ? Number(item.qty) : 1,
              stock: Number.isInteger(Number(item.stock)) && Number(item.stock) >= 0 ? Number(item.stock) : 0,
              maxStock: Number.isInteger(Number(item.maxStock)) && Number(item.maxStock) >= 0 ? Number(item.maxStock) : 999,
              imagen: safeImageUrl(item.imagen),
            }))
        : [];
      // Actualizar el array cart del módulo cart.js
      cart.length = 0;
      cart.push(...cleanCart);
    }
  } catch (_) { 
    cart.length = 0; 
  }
  renderCart();
}