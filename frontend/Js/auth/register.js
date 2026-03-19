/**
 * AUTENTICACIÓN - REGISTRO
 * ========================
 * Maneja el proceso de creación de cuenta:
 * - Validación de campos (email, contraseñas, etc.)
 * - Petición a la API
 * - Inicio de sesión automático tras registro exitoso
 */

import { API_REG } from '../config/constants.js';
import { showAlert, clearAlert } from '../ui/alerts.js';
import { showToast } from '../ui/toast.js';
import { updateNavbarUser } from './session.js';
import { renderCart } from '../cart/render.js';
import { setAuthMode } from './ui.js';

function saveDisplayName(email, name) {
  if (!email || !name) return;
  try {
    const raw = localStorage.getItem('tn_user_names');
    const map = raw ? JSON.parse(raw) : {};
    map[email.trim().toLowerCase()] = name.trim();
    localStorage.setItem('tn_user_names', JSON.stringify(map));
  } catch (_) {}
}

export async function handleRegister() {
  clearAlert('registerAlert');
  
  const nombre = (document.getElementById('registerName')?.value ?? '').trim();
  const email  = (document.getElementById('registerEmail')?.value ?? '').trim();
  const pass1  =  document.getElementById('registerPassword')?.value ?? '';
  const pass2  =  document.getElementById('registerPassword2')?.value ?? '';

  if (!nombre || !email || !pass1 || !pass2) { 
    showAlert('registerAlert', 'Completa todos los campos.', 'warning'); 
    return; 
  }
  if (!/^\S+@\S+\.\S+$/.test(email))         { 
    showAlert('registerAlert', 'Email no válido.', 'warning'); 
    return; 
  }
  if (pass1.length < 6)                        { 
    showAlert('registerAlert', 'La contraseña debe tener al menos 6 caracteres.', 'warning'); 
    return; 
  }
  if (pass1 !== pass2)                         { 
    showAlert('registerAlert', 'Las contraseñas no coinciden.', 'warning'); 
    return; 
  }

  const btn     = document.getElementById('registerBtn');
  const spinner = document.getElementById('registerSpinner');
  const btnText = document.getElementById('registerBtnText');
  
  if (btn) btn.disabled = true;
  spinner?.classList.remove('d-none');
  if (btnText) btnText.textContent = 'Creando…';

  try {
    const res = await fetch(API_REG, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nombre, email, password: pass1 }),
    });
    
    let data = {};
    try { data = await res.json(); } catch (_) {}
    
    if (!res.ok) { 
      showAlert('registerAlert', data.mensaje ?? data.message ?? data.error ?? 'No se pudo crear la cuenta.', 'danger'); 
      return; 
    }

    const name = data.nombre ?? data.name ?? nombre;
    const role = data.rol    ?? data.role ?? 'CLIENTE';
    saveDisplayName(email, name);
    
    sessionStorage.setItem('tn_user',    name);
    sessionStorage.setItem('tn_role',    role);
    sessionStorage.setItem('tn_user_id', String(data.id ?? ''));
    sessionStorage.setItem('tn_token',   data.token ?? '');
    
    bootstrap.Modal.getInstance(document.getElementById('loginModal'))?.hide();
    updateNavbarUser(name, role);
    renderCart();
    showToast(`Cuenta creada. ¡Bienvenido/a, ${name}!`, 'success');
    
    ['registerName','registerEmail','registerPassword','registerPassword2'].forEach(id => {
      const el = document.getElementById(id); if (el) el.value = '';
    });
    
    setAuthMode('login');
  } catch (_) {
    showAlert('registerAlert', 'No se pudo conectar con el servidor.', 'danger');
  } finally {
    if (btn) btn.disabled = false;
    spinner?.classList.add('d-none');
    if (btnText) btnText.textContent = 'Crear cuenta';
  }
}