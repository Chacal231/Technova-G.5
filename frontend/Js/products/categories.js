/**
 * GESTOR DE CATEGORÍAS
 * ====================
 * Extrae las categorías únicas de los productos
 * y las carga en el selector del filtro.
 */

export function populateCategories(products) {
  const select = document.getElementById('categorySelect');
  if (!select) return;
  
  const cats = [...new Set(products.map(p => p.categoria ?? p.category ?? 'General').filter(Boolean))].sort();
  
  select.innerHTML = '<option value="">Todas las categorías</option>';
  cats.forEach(c => {
    const o = document.createElement('option');
    o.value = c; 
    o.textContent = c;
    select.appendChild(o);
  });
}