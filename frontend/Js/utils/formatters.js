/**
 * UTILIDADES DE FORMATEO
 * ======================
 * Funciones para formatear moneda y escapar HTML
 */

export function fmt(v) {
  return new Intl.NumberFormat('es-ES', { style:'currency', currency:'EUR' }).format(v);
}

export function esc(str) {
  return String(str)
    .replace(/&/g,'&amp;')
    .replace(/</g,'&lt;')
    .replace(/>/g,'&gt;')
    .replace(/"/g,'&quot;')
    .replace(/'/g,'&#039;');
}