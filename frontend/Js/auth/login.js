/**
 * AUTENTICACIÓN - LOGIN
 * =====================
 * Maneja el proceso de inicio de sesión:
 * - Validación de campos
 * - Petición a la API
 * - Almacenamiento de sesión
 */

import { API_LOGIN } from '../config/constants.js';
import { showAlert, clearAlert } from '../ui/alerts.js';
import { showToast } from '../ui/toast.js';
import { updateNavbarUser } from './session.js';
import { renderCart } from '../cart/render.js';

function saveDisplayName(email, name) {
  if (!email || !name) return;
  try {
    const emailKey = email.trim().toLowerCase();
    const alias = email.split('@')[0].trim().toLowerCase();
    const cleanName = name.trim();
    if (!cleanName) return;

    const raw = localStorage.getItem('tn_user_names');
    const map = raw ? JSON.parse(raw) : {};
    const current = (map[emailKey] ?? '').trim();
    const cleanLower = cleanName.toLowerCase();
    const currentLower = current.toLowerCase();

    // No sobrescribimos un nombre real por el alias del correo.
    if (cleanLower === alias && current && currentLower !== alias) {
      return;
    }

    map[emailKey] = cleanName;
    localStorage.setItem('tn_user_names', JSON.stringify(map));
  } catch (_) {}
}

function getSavedDisplayName(email) {
  if (!email) return null;
  try {
    const raw = localStorage.getItem('tn_user_names');
    const map = raw ? JSON.parse(raw) : {};
    return map[email.trim().toLowerCase()] ?? null;
  } catch (_) {
    return null;
  }
}

export async function handleLogin() {
  const email    = (document.getElementById('loginEmail')?.value ?? '').trim();
  const password =  document.getElementById('loginPassword')?.value ?? '';
  
  clearAlert('loginAlert');
  clearLoginInputError();
  
  if (!email || !password) { 
    showAlert('loginAlert', 'Completa todos los campos.', 'warning'); 
    return; 
  }

  const btn     = document.getElementById('loginBtn');
  const spinner = document.getElementById('loginSpinner');
  const btnText = document.getElementById('loginBtnText');
  
  if (btn) btn.disabled = true;
  spinner?.classList.remove('d-none');
  if (btnText) btnText.textContent = 'Entrando…';

  try {
    const res  = await fetch(API_LOGIN, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    const data = await res.json();
    
    if (res.ok) {
      const backendNameRaw = data.nombre ?? data.name ?? data.username ?? '';
      const backendName = String(backendNameRaw).trim();
      const savedName = getSavedDisplayName(email)?.trim() ?? '';
      const emailAlias = email.split('@')[0];
      const aliasLower = emailAlias.toLowerCase();
      const backendLower = backendName.toLowerCase();

      const name = backendName
        ? ((savedName && backendLower === aliasLower) ? savedName : backendName)
        : (savedName || emailAlias);
      const role = data.rol    ?? data.role ?? 'CLIENTE';
      saveDisplayName(email, name);
      
      sessionStorage.setItem('tn_user',  name);
      sessionStorage.setItem('tn_role',  role);
      sessionStorage.setItem('tn_user_id', String(data.id ?? ''));
      sessionStorage.setItem('tn_token', data.token ?? '');
      
      bootstrap.Modal.getInstance(document.getElementById('loginModal'))?.hide();
      updateNavbarUser(name, role);
      renderCart();
      showToast(`¡Bienvenido/a, ${name}!`, 'success');
    } else {
      showAlert('loginAlert', data.mensaje ?? data.message ?? data.error ?? 'Credenciales incorrectas.', 'danger');
      setLoginInputError();
    }
  } catch (_) {
    showAlert('loginAlert', 'No se pudo conectar con el servidor.', 'danger');
  } finally {
    if (btn) btn.disabled = false;
    spinner?.classList.add('d-none');
    if (btnText) btnText.textContent = 'Entrar';
  }
}

function setLoginInputError() {
  document.getElementById('loginEmail')?.closest('.auth-input-wrap')?.classList.add('input-error');
  document.getElementById('loginPassword')?.closest('.auth-input-wrap')?.classList.add('input-error');
}

function clearLoginInputError() {
  document.getElementById('loginEmail')?.closest('.auth-input-wrap')?.classList.remove('input-error');
  document.getElementById('loginPassword')?.closest('.auth-input-wrap')?.classList.remove('input-error');
}

export function togglePassword() {
  const inp = document.getElementById('loginPassword');
  const ico = document.getElementById('togglePassIcon');
  if (!inp) return;
  const show = inp.type === 'password';
  inp.type = show ? 'text' : 'password';
  if (ico) ico.className = show ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill';
}