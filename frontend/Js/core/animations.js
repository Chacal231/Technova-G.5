/**
 * ANIMACIONES GLOBALES
 * ====================
 * Efectos visuales de la aplicación:
 * - Cursor glow que sigue al ratón
 * - Partículas flotantes en el hero
 * - Efecto parallax en elementos del hero
 * - Animación de entrada (intro)
 */

export function initCursorGlow() {
  const el = document.getElementById('cursorGlow');
  if (!el) return;
  document.addEventListener('mousemove', e => {
    el.style.transform = `translate(${e.clientX}px, ${e.clientY}px) translate(-50%, -50%)`;
    el.style.left = '0'; el.style.top = '0';
  });
}

export function initHeroParticles() {
  const wrap = document.getElementById('heroParticles');
  if (!wrap) return;
  const total = 70;
  for (let i = 0; i < total; i++) {
    const p = document.createElement('div');
    p.className = 'particle';
    const size = Math.random() * 2.2 + 0.8;
    const duration = Math.random() * 10 + 8;
    const delay = Math.random() * 10;
    p.style.cssText = `
      top: 100%;
      left: ${Math.random() * 100}%;
      width: ${size}px;
      height: ${size}px;
      animation-duration: ${duration}s;
      animation-delay: ${delay}s;
      opacity: ${Math.random() * 0.7 + 0.15};
    `;
    wrap.appendChild(p);
  }
}

export function initHeroParallax() {
  const hero = document.getElementById('hero');
  if (!hero) return;
  const orbs = hero.querySelectorAll('.hero-gradient-orb');
  const cards = hero.querySelectorAll('.hero-card-float');

  hero.addEventListener('pointermove', e => {
    const rect = hero.getBoundingClientRect();
    const x = (e.clientX - rect.left) / rect.width - 0.5;
    const y = (e.clientY - rect.top) / rect.height - 0.5;

    orbs.forEach((orb, i) => {
      const intensity = (i + 1) * 6;
      orb.style.transform = `translate(${x * intensity}px, ${y * intensity}px)`;
    });

    cards.forEach((card, i) => {
      const depth = (i + 1) * 2.5;
      card.style.transform = `translate(${x * depth * -1}px, ${y * depth * -1}px)`;
    });
  });

  hero.addEventListener('pointerleave', () => {
    orbs.forEach(orb => orb.style.transform = '');
    cards.forEach(card => card.style.transform = '');
  });
}

export function initIntro() {
  const overlay = document.getElementById('introOverlay');
  if (!overlay) return;

  document.body.classList.add('intro-active');

  const MIN_DURATION = 2600;
  setTimeout(() => {
    overlay.classList.add('intro-hide');
    document.body.classList.remove('intro-active');
    setTimeout(() => overlay.remove(), 700);
  }, MIN_DURATION);
}