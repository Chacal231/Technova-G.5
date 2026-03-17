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

export async function handleLogin() {
  const email    = (document.getElementById('loginEmail')?.value ?? '').trim();
  const password =  document.getElementById('loginPassword')?.value ?? '';
  
  clearAlert('loginAlert');
  
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
      const name = data.nombre ?? data.name ?? data.username ?? email.split('@')[0];
      const role = data.rol    ?? data.role ?? 'CLIENTE';
      
      sessionStorage.setItem('tn_user',  name);
      sessionStorage.setItem('tn_role',  role);
      sessionStorage.setItem('tn_token', data.token ?? '');
      
      bootstrap.Modal.getInstance(document.getElementById('loginModal'))?.hide();
      updateNavbarUser(name, role);
      showToast(`¡Bienvenido/a, <strong>${name}</strong>!`, 'success');
    } else {
      showAlert('loginAlert', data.mensaje ?? data.message ?? data.error ?? 'Credenciales incorrectas.', 'danger');
    }
  } catch (_) {
    showAlert('loginAlert', 'No se pudo conectar con el servidor.', 'danger');
  } finally {
    if (btn) btn.disabled = false;
    spinner?.classList.add('d-none');
    if (btnText) btnText.textContent = 'Entrar';
  }
}

export function togglePassword() {
  const inp = document.getElementById('loginPassword');
  const ico = document.getElementById('togglePassIcon');
  if (!inp) return;
  const show = inp.type === 'password';
  inp.type = show ? 'text' : 'password';
  if (ico) ico.className = show ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill';
}