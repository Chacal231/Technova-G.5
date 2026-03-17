/**
 * LÓGICA PRINCIPAL DEL CARRITO
 * ============================
 * Funciones para añadir, modificar cantidades y eliminar productos.
 * El estado del carrito se mantiene en el array `cart`.
 */

import { saveCart } from './storage.js';
import { renderCart } from './render.js';
import { showToast } from '../ui/toast.js';

export let cart = [];

export function addToCart(product) {
  const idx = cart.findIndex(i => String(i.id) === String(product.id));
  if (idx !== -1) {
    cart[idx].qty += 1;
  } else {
    cart.push({ ...product, qty: 1 });
  }
  saveCart();
  renderCart();
  showToast(`<strong>${product.nombre}</strong> añadido al carrito`, 'success');
  bootstrap.Offcanvas.getOrCreateInstance(document.getElementById('cartOffcanvas')).show();
}

export function setQty(productId, delta) {
  const idx = cart.findIndex(i => String(i.id) === String(productId));
  if (idx === -1) return;
  const newQty = cart[idx].qty + delta;
  if (newQty <= 0) {
    cart.splice(idx, 1);
  } else {
    cart[idx].qty = newQty;
  }
  saveCart();
  renderCart();
}

export function removeFromCart(productId) {
  cart = cart.filter(i => String(i.id) !== String(productId));
  saveCart();
  renderCart();
}

export function clearCart() {
  if (!cart.length) return;
  cart = [];
  saveCart();
  renderCart();
  showToast('Carrito vaciado', 'info');
}