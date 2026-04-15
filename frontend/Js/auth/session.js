/**
 * GESTIÓN DE SESIÓN
 * =================
 * Controla el estado de la sesión del usuario:
 * - Restaurar sesión desde sessionStorage
 * - Cerrar sesión
 * - Actualizar UI según estado (logueado/no logueado)
 */

import { renderCart } from '../cart/render.js';
import { showToast } from '../ui/toast.js';

export function restoreSession() {
  const user = sessionStorage.getItem('tn_user');
  const role = sessionStorage.getItem('tn_role');
  if (user) updateNavbarUser(user, role ?? 'CLIENTE');
}

export function handleLogout() {
  ['tn_user','tn_role','tn_user_id','tn_token'].forEach(k => sessionStorage.removeItem(k));
  updateNavbarGuest();
  renderCart();
  showToast('Sesión cerrada.', 'info');
}

export function updateNavbarUser(name, role) {
  document.getElementById('navLoginItem')?.classList.add('d-none');
  document.getElementById('navUserItem')?.classList.remove('d-none');
  document.getElementById('navLogoutItem')?.classList.remove('d-none');
  
  const nameEl = document.getElementById('navUserName');
  if (nameEl) nameEl.textContent = name;
  
  const av = document.getElementById('navUserAvatar');
  if (av) av.textContent = name.charAt(0).toUpperCase();
  
  const normalizedRole = (role ?? '').toUpperCase();
  const panelItem = document.getElementById('navRolePanelItem');
  const panelText = document.getElementById('navRolePanelText');
  const hasPanel = normalizedRole === 'ADMIN' || normalizedRole === 'OFICINA';
  panelItem?.classList.toggle('d-none', !hasPanel);
  if (panelText) {
    panelText.textContent = normalizedRole === 'ADMIN' ? 'Panel Admin' : 'Panel Oficina';
  }
}

export function updateNavbarGuest() {
  document.getElementById('navLoginItem')?.classList.remove('d-none');
  document.getElementById('navUserItem')?.classList.add('d-none');
  document.getElementById('navRolePanelItem')?.classList.add('d-none');
  document.getElementById('navLogoutItem')?.classList.add('d-none');
}