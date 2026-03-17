/**
 * MODAL DE DETALLE DE PRODUCTO
 * ============================
 * Muestra información detallada del producto en un modal
 * y permite añadirlo al carrito desde ahí.
 */

import { addToCart } from '../cart/cart.js';
import { esc, fmt } from '../utils/formatters.js';

export function showProductDetail({ id, nombre, precio, stock, cat, imagen, desc }) {
  const agotado  = stock === 0;
  const stockBajo= !agotado && stock <= 5;

  const imgHTML = imagen
    ? `<img src="${imagen}" alt="${esc(nombre)}" style="max-height:200px;max-width:100%;object-fit:contain;">`
    : `<span style="font-size:5rem;color:var(--text3);opacity:.2;"><i class="bi bi-box-seam"></i></span>`;

  let stockLabel = '';
  if (agotado)        stockLabel = `<span style="color:var(--danger)"><i class="bi bi-x-circle me-1"></i>Sin stock</span>`;
  else if (stockBajo) stockLabel = `<span style="color:var(--warning)"><i class="bi bi-exclamation-triangle me-1"></i>Últimas ${stock} unidades</span>`;
  else                stockLabel = `<span style="color:var(--success)"><i class="bi bi-check-circle me-1"></i>${stock} unidades disponibles</span>`;

  document.getElementById('productModalBody').innerHTML = `
    <div class="p-4">
      <div class="row g-4 align-items-center">
        <div class="col-md-5 text-center" style="background:var(--surface2);border-radius:14px;padding:32px;">
          ${imgHTML}
        </div>
        <div class="col-md-7">
          <p style="font-family:var(--font-m);font-size:10px;color:var(--accent);letter-spacing:2px;text-transform:uppercase;margin-bottom:8px">${esc(cat)}</p>
          <h3 style="font-family:var(--font-d);font-weight:700;color:var(--text);font-size:1.6rem;line-height:1.2;margin-bottom:12px">${esc(nombre)}</h3>
          ${desc ? `<p style="font-size:13px;color:var(--text2);margin-bottom:14px;line-height:1.7">${esc(desc)}</p>` : ''}
          <p style="font-family:var(--font-d);font-size:2rem;font-weight:700;color:var(--accent);margin-bottom:10px">${fmt(precio)}</p>
          <p style="font-size:13px;margin-bottom:20px">${stockLabel}</p>
          <button class="btn-add-cart" id="modalAddBtn" ${agotado ? 'disabled' : ''} style="max-width:240px">
            ${agotado ? '<i class="bi bi-x-circle me-1"></i>No disponible' : '<i class="bi bi-bag-plus me-1"></i>Añadir al carrito'}
          </button>
        </div>
      </div>
    </div>
  `;

  if (!agotado) {
    document.getElementById('modalAddBtn')?.addEventListener('click', () => {
      addToCart({ id, nombre, precio, imagen, categoria: cat });
      bootstrap.Modal.getInstance(document.getElementById('productModal'))?.hide();
    });
  }
  new bootstrap.Modal(document.getElementById('productModal')).show();
}