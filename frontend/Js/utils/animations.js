/**
 * UTILIDADES DE ANIMACIÓN
 * =======================
 * Funciones para animar números (contadores)
 */

export function animateNumber(el, from, to, duration) {
  const start = performance.now();
  
  const update = (time) => {
    const progress = Math.min((time - start) / duration, 1);
    el.textContent = Math.round(from + (to - from) * progress);
    if (progress < 1) requestAnimationFrame(update);
  };
  
  requestAnimationFrame(update);
}