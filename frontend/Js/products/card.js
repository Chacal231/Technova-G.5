/**
 * CONSTRUCTOR DE TARJETAS DE PRODUCTO
 * ===================================
 * Crea el HTML de cada tarjeta de producto y maneja
 * los eventos de añadir al carrito y ver detalle.
 */

import { addToCart } from '../cart/cart.js';
import { showProductDetail } from './detail.js';
import { esc, fmt } from '../utils/formatters.js';

export function buildCard(p, index) {
  const id       = p.id ?? p._id ?? index;
  const nombre   = p.nombre ?? p.name ?? 'Sin nombre';
  const precio   = parseFloat(p.precio ?? p.price ?? 0);
  const stock    = p.stock != null ? Number(p.stock) : (p.cantidad != null ? Number(p.cantidad) : 1);
  const cat      = p.categoria ?? p.category ?? 'General';
  const imagen   = p.imagen ?? p.image ?? p.imageUrl ?? null;
  const desc     = p.descripcion ?? p.description ?? '';
  const agotado  = stock === 0;
  const stockBajo= !agotado && stock <= 5;
  const delay    = (index % 12) * 50;

  const wrap = document.createElement('div');

  const imgHTML = imagen
    ? `<img src="${imagen}" alt="${esc(nombre)}" loading="lazy" />`
    : `<span class="card-img-placeholder"><i class="bi bi-box-seam"></i></span>`;

  let badgeHTML = '';
  if (agotado)        badgeHTML = `<span class="badge-agotado">AGOTADO</span>`;
  else if (stockBajo) badgeHTML = `<span class="badge-stock low">Últimas ${stock}</span>`;
  else                badgeHTML = `<span class="badge-stock ok">En stock</span>`;

  wrap.innerHTML = `
    <article class="product-card ${agotado ? 'out-of-stock' : ''}"
             style="animation-delay:${delay}ms" role="button" tabindex="0">
      <div class="card-img-wrap">
        ${imgHTML}
        ${badgeHTML}
      </div>
      <div class="card-body">
        <p class="card-cat">${esc(cat)}</p>
        <h3 class="card-name">${esc(nombre)}</h3>
        <p class="card-price">${fmt(precio)}</p>
        <p class="card-stock-text">
          <i class="bi bi-box me-1"></i>
          ${agotado ? 'Sin stock' : `${stock} ud${stock !== 1 ? 's' : ''}.`}
        </p>
        <button class="btn-add-cart" data-id="${id}"
                ${agotado ? 'disabled' : ''}>
          ${agotado
            ? '<i class="bi bi-x-circle me-1"></i>No disponible'
            : '<i class="bi bi-bag-plus me-1"></i>Añadir al carrito'}
        </button>
      </div>
    </article>
  `;

  wrap.querySelector('.btn-add-cart')?.addEventListener('click', e => {
    e.stopPropagation();
    if (agotado) return;
    addToCart({ id, nombre, precio, imagen, categoria: cat });

    const btn = e.currentTarget;
    btn.classList.add('added');
    const orig = btn.innerHTML;
    btn.innerHTML = '<i class="bi bi-check-lg me-1"></i>¡Añadido!';
    setTimeout(() => { 
      btn.classList.remove('added'); 
      btn.innerHTML = orig; 
    }, 1200);
  });

  wrap.querySelector('.product-card')?.addEventListener('click', e => {
    if (!e.target.closest('.btn-add-cart'))
      showProductDetail({ id, nombre, precio, stock, cat, imagen, desc });
  });

  return wrap;
}