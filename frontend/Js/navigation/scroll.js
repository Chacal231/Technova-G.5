/**
 * GESTIÓN DEL SCROLL
 * ==================
 * Controla el comportamiento del navbar al hacer scroll
 * y la navegación suave a secciones de la página.
 */

export function initNavbarScroll() {
  const nav = document.getElementById('mainNavbar');
  window.addEventListener('scroll', () => {
    nav?.classList.toggle('scrolled', window.scrollY > 50);
  }, { passive: true });
}

export function initSmoothScroll() {
  const links = document.querySelectorAll('a[href^="#"]');
  if (!links.length) return;

  const getOffset = () => {
    const nav = document.getElementById('mainNavbar');
    return (nav?.offsetHeight ?? 70) + 8;
  };

  links.forEach(link => {
    const href = link.getAttribute('href');
    if (!href || href === '#') return;
    link.addEventListener('click', ev => {
      const target = document.querySelector(href);
      if (!target) return;
      ev.preventDefault();
      const offset = getOffset();
      const targetY = target.getBoundingClientRect().top + window.scrollY - offset;
      smoothScrollTo(targetY, 700);
    });
  });
}

function smoothScrollTo(targetY, duration) {
  const startY = window.scrollY;
  const distance = targetY - startY;
  const startTime = performance.now();

  const easeOutCubic = t => 1 - Math.pow(1 - t, 3);

  function step(now) {
    const elapsed = now - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const eased = easeOutCubic(progress);
    window.scrollTo(0, startY + distance * eased);
    if (progress < 1) requestAnimationFrame(step);
  }

  requestAnimationFrame(step);
}