/**
 * SISTEMA DE TOAST NOTIFICATIONS
 * ==============================
 * Muestra notificaciones temporales en la esquina inferior derecha
 */

export function showToast(message, type = 'info') {
  let container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    document.body.appendChild(container);
  }
  
  const t = document.createElement('div');
  t.className = `tn-toast ${type}`;
  t.innerHTML = message;
  container.appendChild(t);
  
  setTimeout(() => {
    t.style.opacity = '0';
    t.style.transform = 'translateX(30px)';
    setTimeout(() => t.remove(), 350);
  }, 3200);
}