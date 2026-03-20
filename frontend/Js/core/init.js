/**
 * INICIALIZACIÓN PRINCIPAL
 * ========================
 * Punto de entrada de la aplicación.
 * Se ejecuta cuando el DOM está completamente cargado.
 * Importa y coordina todas las funciones de inicialización.
 */

import { initTheme } from './theme.js';
import { initCursorGlow, initHeroParticles, initHeroParallax, initIntro } from './animations.js';
import { initNavbarScroll, initSmoothScroll } from '../navigation/scroll.js';
import { restoreSession } from '../auth/session.js';
import { restoreCart } from '../cart/storage.js';
import { loadProducts } from '../products/api.js';
import { setupEvents } from '../navigation/events.js';
import { initRtx3D } from '../hero/rtx3d.js';

export function initApp() {
  initIntro();
  initTheme();
  restoreSession();
  restoreCart();
  loadProducts();
  setupEvents();
  initNavbarScroll();
  initCursorGlow();
  initHeroParticles();
  initSmoothScroll();
  initHeroParallax();
  initRtx3D();
}

// Auto-ejecutar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', initApp);