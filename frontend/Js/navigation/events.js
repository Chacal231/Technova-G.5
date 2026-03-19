/**
 * EVENT LISTENERS PRINCIPALES
 * ===========================
 * Registra todos los event listeners de la aplicación:
 * - Filtros de búsqueda
 * - Cambio de vista (grid/list)
 * - Eventos de autenticación
 * - Enlaces del footer
 */

import { filterAndRender, resetFilters } from '../products/catalog.js';
import { setView } from '../ui/viewToggle.js';
import { toggleTheme } from '../core/theme.js';
import { handleLogin, togglePassword } from '../auth/login.js';
import { handleRegister } from '../auth/register.js';
import { handleLogout } from '../auth/session.js';
import { setAuthMode } from '../auth/ui.js';
import { handleCheckout } from '../cart/checkout.js';
import { clearCart } from '../cart/cart.js';
import { showToast } from '../ui/toast.js';

export function setupEvents() {
  // Filtros
  document.getElementById('searchInput')   ?.addEventListener('input',  filterAndRender);
  document.getElementById('categorySelect')?.addEventListener('change', filterAndRender);
  document.getElementById('minPrice')      ?.addEventListener('input',  filterAndRender);
  document.getElementById('maxPrice')      ?.addEventListener('input',  filterAndRender);
  document.getElementById('sortSelect')    ?.addEventListener('change', filterAndRender);
  document.getElementById('clearFiltersBtn')?.addEventListener('click', resetFilters);

  // Vista
  document.getElementById('viewGrid')?.addEventListener('click', () => setView(false));
  document.getElementById('viewList')?.addEventListener('click', () => setView(true));

  // Tema
  document.getElementById('themeToggle')?.addEventListener('click', toggleTheme);

  // Auth — login
  document.getElementById('loginBtn')   ?.addEventListener('click', handleLogin);
  document.getElementById('logoutBtn')  ?.addEventListener('click', handleLogout);
  document.getElementById('goRegisterLink')?.addEventListener('click', () => setAuthMode('register'));
  document.getElementById('goLoginLink')?.addEventListener('click', () => setAuthMode('login'));
  document.getElementById('togglePass') ?.addEventListener('click', togglePassword);

  // Auth — register
  document.getElementById('registerBtn')?.addEventListener('click', handleRegister);

  // Enter en campos
  ['loginEmail','loginPassword'].forEach(id =>
    document.getElementById(id)?.addEventListener('keydown', e => { if (e.key === 'Enter') handleLogin(); })
  );
  ['registerName','registerEmail','registerPassword','registerPassword2'].forEach(id =>
    document.getElementById(id)?.addEventListener('keydown', e => { if (e.key === 'Enter') handleRegister(); })
  );

  // Footer links
  document.getElementById('supportEmailLink')?.addEventListener('click', e => {
    e.preventDefault();
    window.location.href = 'mailto:soporte@technova.com?subject=Soporte%20TechNova';
  });
  document.getElementById('supportHelpLink')?.addEventListener('click', e => {
    e.preventDefault(); showToast('Centro de ayuda (demo)', 'info');
  });
  document.getElementById('supportContactLink')?.addEventListener('click', e => {
    e.preventDefault(); showToast('Contacto (demo)', 'info');
  });

  // Carrito — vaciar y checkout
  document.getElementById('emptyCartBtn')?.addEventListener('click', clearCart);
  document.getElementById('checkoutBtn')?.addEventListener('click', handleCheckout);
}