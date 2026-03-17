/**
 * CATÁLOGO DE PRODUCTOS
 * =====================
 * Filtrado, ordenación y renderizado del catálogo.
 * Incluye la lógica de búsqueda por texto, categoría y precio.
 */

import { allProducts } from './api.js';
import { buildCard } from './card.js';
import { esc, fmt } from '../utils/formatters.js';

let isListView = false;

export function filterAndRender() {
  const term = (document.getElementById('searchInput')?.value ?? '').trim().toLowerCase();
  const cat  = (document.getElementById('categorySelect')?.value ?? '').toLowerCase();
  const minP = parseFloat(document.getElementById('minPrice')?.value ?? '');
  const maxP = parseFloat(document.getElementById('maxPrice')?.value ?? '');
  const sort = document.getElementById('sortSelect')?.value ?? 'relevance';

  const hasFilter = term || cat || !isNaN(minP) || !isNaN(maxP);
  const clearBtn  = document.getElementById('clearFiltersBtn');
  clearBtn?.classList.toggle('d-none', !hasFilter);

  const filtered = allProducts.filter(p => {
    const name  = (p.nombre || p.name || '').toLowerCase();
    const desc  = (p.descripcion || p.description || '').toLowerCase();
    const catP  = (p.categoria || p.category || '').toLowerCase();
    const price = parseFloat(p.precio ?? p.price ?? 0);
    return (
      (!term || name.includes(term) || desc.includes(term)) &&
      (!cat  || catP === cat) &&
      (isNaN(minP) || price >= minP) &&
      (isNaN(maxP) || price <= maxP)
    );
  });

  const sorted = [...filtered];
  if (sort === 'priceAsc')  sorted.sort((a,b) => (a.precio??a.price??0)-(b.precio??b.price??0));
  if (sort === 'priceDesc') sorted.sort((a,b) => (b.precio??b.price??0)-(a.precio??a.price??0));
  if (sort === 'nameAsc')   sorted.sort((a,b) => String(a.nombre??a.name??'').localeCompare(String(b.nombre??b.name??''),'es'));

  renderProducts(sorted);
}

export function resetFilters() {
  ['searchInput','minPrice','maxPrice'].forEach(id => { 
    const el = document.getElementById(id); 
    if (el) el.value = ''; 
  });
  const cat  = document.getElementById('categorySelect');  
  if (cat)  cat.value  = '';
  const sort = document.getElementById('sortSelect');       
  if (sort) sort.value = 'relevance';
  document.getElementById('clearFiltersBtn')?.classList.add('d-none');
  filterAndRender();
}

export function renderProducts(products) {
  const container  = document.getElementById('catalogo-container');
  const emptyState = document.getElementById('emptyState');
  const resultCount= document.getElementById('resultCount');
  if (!container) return;

  container.querySelectorAll('.sk-col').forEach(el => el.remove());

  const total = products.length;
  if (resultCount) resultCount.textContent = `${total} producto${total !== 1 ? 's' : ''} encontrado${total !== 1 ? 's' : ''}`;

  if (total === 0) {
    container.innerHTML = '';
    emptyState?.classList.remove('d-none');
    return;
  }
  emptyState?.classList.add('d-none');
  container.innerHTML = '';

  if (isListView) {
    container.className = 'products-list';
    container.style.cssText = 'display:flex;flex-direction:column;gap:14px;';
  } else {
    container.className = 'products-grid';
    container.style.cssText = '';
  }

  products.forEach((p, i) => container.appendChild(buildCard(p, i)));
}

export function setListView(value) {
  isListView = value;
}