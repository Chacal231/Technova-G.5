/**
 * PERSISTENCIA DEL CARRITO
 * ========================
 * Guarda y restaura el carrito usando sessionStorage.
 * Los datos persisten mientras la pestaña esté abierta.
 */

import { cart } from './cart.js';
import { renderCart } from './render.js';

export function saveCart() {
  try { 
    sessionStorage.setItem('tn_cart', JSON.stringify(cart)); 
  } catch (_) {}
}

export function restoreCart() {
  try {
    const s = sessionStorage.getItem('tn_cart');
    if (s) {
      const restoredCart = JSON.parse(s);
      // Actualizar el array cart del módulo cart.js
      cart.length = 0;
      cart.push(...restoredCart);
    }
  } catch (_) { 
    cart.length = 0; 
  }
  renderCart();
}