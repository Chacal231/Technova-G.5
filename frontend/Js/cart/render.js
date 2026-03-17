/**
 * RENDERIZADO DEL CARRITO
 * =======================
 * Actualiza la interfaz del carrito: lista de productos,
 * badges, subtotales y totales con IVA.
 */

import { cart, setQty, removeFromCart } from './cart.js';
import { IVA } from '../config/constants.js';
import { esc, fmt } from '../utils/formatters.js';

export function renderCart() {
  const listWrap  = document.getElementById('cartItemsList');
  const emptyEl   = document.getElementById('cartEmpty');
  const footerEl  = document.getElementById('cartFooter');
  const badgeEl   = document.getElementById('cartBadge');
  const countEl   = document.getElementById('cartItemCount');
  if (!listWrap) return;

  const totalUnits = cart.reduce((a, i) => a + i.qty, 0);
  
  if (badgeEl) {
    if (totalUnits > 0) {
      badgeEl.textContent = totalUnits > 99 ? '99+' : String(totalUnits);
      badgeEl.classList.remove('d-none');
    } else {
      badgeEl.classList.add('d-none');
    }
  }
  if (countEl) countEl.textContent = `${totalUnits} artículo${totalUnits !== 1 ? 's' : ''}`;

  listWrap.innerHTML = '';

  if (cart.length === 0) {
    emptyEl?.classList.remove('d-none');
    footerEl?.classList.add('d-none');
    return;
  }

  emptyEl?.classList.add('d-none');
  footerEl?.classList.remove('d-none');

  cart.forEach(item => {
    const el = document.createElement('div');
    el.className = 'cart-item';
    el.dataset.id = String(item.id);

    el.innerHTML = `
      <div class="cart-item-img">
        ${item.imagen
          ? `<img src="${item.imagen}" alt="${esc(item.nombre)}">`
          : `<i class="bi bi-box-seam no-img"></i>`}
      </div>
      <div class="cart-item-info">
        <p class="cart-item-name">${esc(item.nombre)}</p>
        <p class="cart-item-cat">${esc(item.categoria ?? 'General')}</p>
        <p class="cart-item-unit">${fmt(item.precio)} / ud.</p>
        <div class="qty-controls">
          <button class="btn-qty btn-qty-minus" aria-label="Reducir">−</button>
          <span class="qty-value">${item.qty}</span>
          <button class="btn-qty btn-qty-plus" aria-label="Aumentar">+</button>
        </div>
      </div>
      <div class="cart-item-right">
        <span class="cart-item-sub">${fmt(item.precio * item.qty)}</span>
        <button class="btn-remove-item" aria-label="Eliminar">
          <i class="bi bi-trash3"></i>
        </button>
      </div>
    `;

    el.querySelector('.btn-qty-minus')?.addEventListener('click', e => {
      e.stopPropagation();
      setQty(item.id, -1);
    });
    
    el.querySelector('.btn-qty-plus')?.addEventListener('click', e => {
      e.stopPropagation();
      setQty(item.id, +1);
    });
    
    el.querySelector('.btn-remove-item')?.addEventListener('click', e => {
      e.stopPropagation();
      el.style.transition = 'opacity .2s, transform .2s';
      el.style.opacity = '0';
      el.style.transform = 'translateX(20px)';
      setTimeout(() => removeFromCart(item.id), 200);
    });

    listWrap.appendChild(el);
  });

  const subtotal = cart.reduce((a, i) => a + i.precio * i.qty, 0);
  const iva      = subtotal * IVA;
  const total    = subtotal + iva;

  const sub = document.getElementById('cartSubtotal');
  const ivaEl = document.getElementById('cartIVA');
  const totEl = document.getElementById('cartTotal');
  
  if (sub)   sub.textContent   = fmt(subtotal);
  if (ivaEl) ivaEl.textContent = fmt(iva);
  if (totEl) totEl.textContent = fmt(total);
}