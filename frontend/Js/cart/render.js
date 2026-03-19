/**
 * RENDERIZADO DEL CARRITO
 * =======================
 * Actualiza la interfaz del carrito: lista de productos,
 * badges, subtotales y totales (precios ya incluyen IVA).
 */

import { cart, setQty, removeFromCart } from './cart.js';
import { esc, fmt, safeImageUrl } from '../utils/formatters.js';

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
  const checkoutBtn = document.getElementById('checkoutBtn');

  listWrap.innerHTML = '';

  if (cart.length === 0) {
    emptyEl?.classList.remove('d-none');
    footerEl?.classList.add('d-none');
    if (checkoutBtn) {
      checkoutBtn.disabled = true;
      checkoutBtn.classList.add('is-locked');
      checkoutBtn.innerHTML = '<i class="bi bi-lock-fill me-2"></i>Finalizar compra';
    }
    return;
  }

  emptyEl?.classList.add('d-none');
  footerEl?.classList.remove('d-none');

  cart.forEach(item => {
    const qty = Number.isInteger(Number(item.qty)) ? Number(item.qty) : 1;
    const precio = Number(item.precio) || 0;
    const imagenSegura = safeImageUrl(item.imagen);
    const el = document.createElement('div');
    el.className = 'cart-item';
    el.dataset.id = String(item.id);

    el.innerHTML = `
      <div class="cart-item-img">
        ${imagenSegura
          ? `<img src="${esc(imagenSegura)}" alt="${esc(item.nombre)}">`
          : `<i class="bi bi-box-seam no-img"></i>`}
      </div>
      <div class="cart-item-info">
        <p class="cart-item-name">${esc(item.nombre)}</p>
        <p class="cart-item-cat">${esc(item.categoria ?? 'General')}</p>
        <p class="cart-item-unit">${fmt(precio)} / ud.</p>
        <div class="qty-controls">
          <button class="btn-qty btn-qty-minus" aria-label="Reducir">−</button>
          <span class="qty-value">${qty}</span>
          <button class="btn-qty btn-qty-plus" aria-label="Aumentar">+</button>
        </div>
      </div>
      <div class="cart-item-right">
        <span class="cart-item-sub">${fmt(precio * qty)}</span>
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

  const subtotal = cart.reduce((a, i) => a + (Number(i.precio) || 0) * (Number(i.qty) || 0), 0);
  // Los precios de los productos ya incluyen IVA, así que no sumamos un IVA adicional.
  const iva      = 0;
  const total    = subtotal;

  const sub = document.getElementById('cartSubtotal');
  const ivaEl = document.getElementById('cartIVA');
  const totEl = document.getElementById('cartTotal');
  
  if (sub)   sub.textContent   = fmt(subtotal);
  if (ivaEl) ivaEl.textContent = fmt(iva);
  if (totEl) totEl.textContent = fmt(total);
  if (checkoutBtn) {
    const logged = !!sessionStorage.getItem('tn_user_id');
    checkoutBtn.disabled = !logged;
    checkoutBtn.classList.toggle('is-locked', !logged);
    checkoutBtn.innerHTML = logged
      ? '<i class="bi bi-bag-check-fill me-2"></i>Finalizar compra'
      : '<i class="bi bi-lock-fill me-2"></i>Finalizar compra';
  }
}