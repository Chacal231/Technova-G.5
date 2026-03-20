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
  const reducedMotion = window.matchMedia?.('(prefers-reduced-motion: reduce)')?.matches;
  const lowPower = (navigator.hardwareConcurrency && navigator.hardwareConcurrency <= 4)
    || (navigator.deviceMemory && navigator.deviceMemory <= 4);
  const total = reducedMotion ? 16 : (lowPower ? 34 : 52);
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
  const rtxRender = hero.querySelector('.hero-rtx-render');
  const rtxCanvas = hero.querySelector('#rtxCanvas');

  let dragging = false;
  let startX = 0;
  let startY = 0;
  let rotX = 0;
  let rotY = 0;
  let baseRotX = 0;
  let baseRotY = 0;

  const clamp = (v, min, max) => Math.max(min, Math.min(max, v));
  const applyRtx = (tx, ty, rx, ry) => {
    if (!rtxRender) return;
    rtxRender.style.transform = `translate(${tx}px, ${ty}px) rotateX(${rx}deg) rotateY(${ry}deg)`;
  };

  // Fallback drag solo cuando no se usa canvas 3D real.
  if (rtxRender && !rtxCanvas) {
    rtxRender.addEventListener('pointerdown', e => {
      dragging = true;
      startX = e.clientX;
      startY = e.clientY;
      baseRotX = rotX;
      baseRotY = rotY;
      rtxRender.classList.add('is-dragging');
      rtxRender.setPointerCapture?.(e.pointerId);
      e.preventDefault();
    });

    rtxRender.addEventListener('pointermove', e => {
      if (!dragging) return;
      const dx = e.clientX - startX;
      const dy = e.clientY - startY;
      rotY = baseRotY + dx * 0.18;
      rotX = clamp(baseRotX - dy * 0.14, -55, 55);
      applyRtx(0, 0, rotX, rotY);
    });

    const stopDrag = () => {
      dragging = false;
      rtxRender.classList.remove('is-dragging');
      applyRtx(0, 0, rotX, rotY);
    };
    rtxRender.addEventListener('pointerup', stopDrag);
    rtxRender.addEventListener('pointercancel', stopDrag);
    rtxRender.addEventListener('lostpointercapture', stopDrag);
  }

  let raf = 0;
  let pendingX = 0;
  let pendingY = 0;

  const flushParallax = () => {
    raf = 0;
    const x = pendingX;
    const y = pendingY;

    orbs.forEach((orb, i) => {
      const intensity = (i + 1) * 6;
      orb.style.transform = `translate(${x * intensity}px, ${y * intensity}px)`;
    });

    cards.forEach((card, i) => {
      const depth = (i + 1) * 2.5;
      card.style.transform = `translate(${x * depth * -1}px, ${y * depth * -1}px)`;
    });

    if (rtxRender && !dragging && !rtxCanvas) {
      rotX = y * -9;
      rotY = x * 14;
      applyRtx(x * -10, y * -10, rotX, rotY);
    }
  };

  hero.addEventListener('pointermove', e => {
    const rect = hero.getBoundingClientRect();
    pendingX = (e.clientX - rect.left) / rect.width - 0.5;
    pendingY = (e.clientY - rect.top) / rect.height - 0.5;
    if (!raf) raf = requestAnimationFrame(flushParallax);
  });

  hero.addEventListener('pointerleave', () => {
    orbs.forEach(orb => orb.style.transform = '');
    cards.forEach(card => card.style.transform = '');
    if (rtxRender && !dragging && !rtxCanvas) applyRtx(0, 0, rotX, rotY);
  });
}

export function initIntro() {
  const overlay = document.getElementById('introOverlay');
  if (!overlay) return;

  document.body.classList.add('intro-active');
  overlay.classList.add('intro-enter');

  const EXIT_DELAY = 900;
  const MIN_DURATION = 1500;

  setTimeout(() => {
    overlay.classList.add('intro-exit');
  }, EXIT_DELAY);

  setTimeout(() => {
    overlay.classList.add('intro-hide');
    document.body.classList.remove('intro-active');
    setTimeout(() => overlay.remove(), 420);
  }, MIN_DURATION);
}