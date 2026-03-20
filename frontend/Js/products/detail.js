/**
 * MODAL DE DETALLE DE PRODUCTO
 * ============================
 * Muestra información detallada del producto en un modal
 * y permite añadirlo al carrito desde ahí.
 */

import { addToCart } from '../cart/cart.js';
import { esc, fmt, safeImageUrl } from '../utils/formatters.js';

export function showProductDetail({ id, nombre, precio, stock, cat, imagen, desc }) {
  const agotado  = stock === 0;
  const stockBajo= !agotado && stock <= 5;
  const imagenSegura = safeImageUrl(imagen);

  const imgHTML = imagenSegura
    ? `<img src="${esc(imagenSegura)}" alt="${esc(nombre)}" style="max-height:200px;max-width:100%;object-fit:contain;">`
    : `<span style="font-size:5rem;color:var(--text3);opacity:.2;"><i class="bi bi-box-seam"></i></span>`;

  let stockLabel = '';
  if (agotado)        stockLabel = `<span class="pdm-stock is-out"><i class="bi bi-x-circle me-1"></i>Sin stock</span>`;
  else if (stockBajo) stockLabel = `<span class="pdm-stock is-low"><i class="bi bi-exclamation-triangle me-1"></i>Últimas ${stock} unidades</span>`;
  else                stockLabel = `<span class="pdm-stock is-ok"><i class="bi bi-check-circle me-1"></i>${stock} unidades disponibles</span>`;

  document.getElementById('productModalBody').innerHTML = `
    <div class="pdm-wrap p-4">
      <div class="row g-4 align-items-center">
        <div class="col-md-5 text-center pdm-media">
          ${imgHTML}
        </div>
        <div class="col-md-7 pdm-info">
          <p class="pdm-cat">${esc(cat)}</p>
          <h3 class="pdm-name">${esc(nombre)}</h3>
          ${desc ? `<p class="pdm-desc">${esc(desc)}</p>` : ''}
          <p class="pdm-price">${fmt(precio)}</p>
          <p class="pdm-stock-row">${stockLabel}</p>
          <button class="btn-add-cart" id="modalAddBtn" ${agotado ? 'disabled' : ''} style="max-width:240px">
            ${agotado ? '<i class="bi bi-x-circle me-1"></i>No disponible' : '<i class="bi bi-bag-plus me-1"></i>Añadir al carrito'}
          </button>
        </div>
      </div>
    </div>
  `;

  if (!agotado) {
    document.getElementById('modalAddBtn')?.addEventListener('click', () => {
      addToCart({ id, nombre, precio, imagen: imagenSegura, categoria: cat, stock });
      bootstrap.Modal.getInstance(document.getElementById('productModal'))?.hide();
    });
  }
  new bootstrap.Modal(document.getElementById('productModal')).show();
}