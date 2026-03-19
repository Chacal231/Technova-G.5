/**
 * LOGICA PRINCIPAL DEL CARRITO
 * ============================
 * Funciones para anadir, modificar cantidades y eliminar productos.
 * El estado del carrito se mantiene en el array `cart`.
 * La cantidad nunca puede superar el stock disponible del producto.
 */

import { saveCart } from './storage.js';
import { renderCart } from './render.js';
import { showToast } from '../ui/toast.js';
import { allProducts } from '../products/api.js';

export let cart = [];

// Obtiene el stock actual de un producto consultando los datos cargados
function getStockDisponible(productId) {
  const prod = allProducts.find(p => String(p.id) === String(productId));
  return prod != null ? Number(prod.stock) : null;
}

export function addToCart(product) {
  const maxStock = product.stock != null ? Number(product.stock) : getStockDisponible(product.id);
  const idx = cart.findIndex(i => String(i.id) === String(product.id));

  if (idx !== -1) {
    if (maxStock != null && cart[idx].qty >= maxStock) {
      showToast(`Solo quedan <strong>${maxStock}</strong> uds. de este producto.`, 'warning');
      return;
    }
    cart[idx].qty += 1;
    // Actualizar stock almacenado por si cambio
    if (maxStock != null) cart[idx].maxStock = maxStock;
  } else {
    cart.push({ ...product, qty: 1, maxStock: maxStock ?? 999 });
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
    const maxStock = getStockDisponible(productId) ?? cart[idx].maxStock ?? 999;
    if (newQty > maxStock) {
      showToast(`Solo quedan <strong>${maxStock}</strong> uds. de este producto.`, 'warning');
      return;
    }
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
