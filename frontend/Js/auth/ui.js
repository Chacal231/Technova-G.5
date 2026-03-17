/**
 * UI DE AUTENTICACIÓN
 * ===================
 * Controla la interfaz de los formularios de login/registro
 */

export function setAuthMode(mode) {
  const isReg = mode === 'register';
  document.getElementById('loginPane')?.classList.toggle('d-none', isReg);
  document.getElementById('registerPane')?.classList.toggle('d-none', !isReg);
}