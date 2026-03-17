/**
 * SISTEMA DE ALERTAS
 * ==================
 * Muestra mensajes de error/warning/success en los formularios
 */

export function showAlert(id, msg, type) {
  const el = document.getElementById(id);
  if (!el) return;
  el.className = `auth-alert ${type}`;
  el.innerHTML = msg;
  el.classList.remove('d-none');
}

export function clearAlert(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.classList.add('d-none');
  el.innerHTML = '';
}