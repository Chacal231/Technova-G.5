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

export function safeImageUrl(url) {
  if (!url) return null;
  const raw = String(url).trim();
  if (!raw) return null;
  const isLocalAsset =
    raw.startsWith('assets/') ||
    raw.startsWith('./assets/') ||
    raw.startsWith('img/') ||
    raw.startsWith('./img/');
  const isHttp = /^https?:\/\/[^\s]+$/i.test(raw);
  const isDataImage = /^data:image\/[a-z0-9.+-]+;base64,[a-z0-9+/=\s]+$/i.test(raw);
  if (isLocalAsset || isHttp || isDataImage) return raw;
  return null;
}