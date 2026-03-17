/**
 * GESTOR DE TEMAS (Claro/Oscuro)
 * ==============================
 * Maneja el cambio de tema, persistencia en localStorage,
 * y la animación de transición entre temas.
 */

import { THEME_KEY } from '../config/constants.js';

export function initTheme() {
  let t = 'dark';
  try { 
    const s = localStorage.getItem(THEME_KEY); 
    if (s === 'light' || s === 'dark') t = s; 
  } catch (_) {}
  applyTheme(t);
}

export function toggleTheme() {
  const cur = document.documentElement.getAttribute('data-theme') ?? 'dark';
  applyTheme(cur === 'dark' ? 'light' : 'dark');
}

export function applyTheme(t) {
  const root = document.documentElement;
  root.classList.add('theme-transition');
  document.documentElement.setAttribute('data-theme', t);
  try { localStorage.setItem(THEME_KEY, t); } catch (_) {}
  
  const icon = document.getElementById('themeIcon');
  if (icon) icon.className = t === 'dark' ? 'bi bi-moon-stars-fill' : 'bi bi-brightness-high-fill';

  setTimeout(() => {
    root.classList.remove('theme-transition');
  }, 400);
}