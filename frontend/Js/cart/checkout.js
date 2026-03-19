/**
 * CHECKOUT - FINALIZAR COMPRA
 * ===========================
 * Envia al backend solo id_producto + cantidad.
 * El precio y total se calculan en el servidor (nunca confiamos en el cliente).
 */

import { API_PEDIDOS } from '../config/constants.js';
import { cart, clearCart } from './cart.js';
import { showToast } from '../ui/toast.js';

function getUsuarioIdSesion() {
  const raw = sessionStorage.getItem('tn_user_id');
  const id = Number(raw);
  return Number.isInteger(id) && id > 0 ? id : null;
}

function buildPayload() {
  return {
    id_usuario: getUsuarioIdSesion(),
    productos: cart.map(item => ({
      id_producto: Number(item.id),
      cantidad: Number(item.qty),
    })),
  };
}

export async function handleCheckout() {
  const btn = document.getElementById('checkoutBtn');

  if (!cart.length) {
    showToast('Tu carrito está vacío.', 'warning');
    return;
  }

  // Si no hay sesion, abrir modal de login y avisar
  const idUsuario = getUsuarioIdSesion();
  if (!idUsuario) {
    showToast('Inicia sesión para finalizar tu compra.', 'warning');
    // Cerrar offcanvas del carrito y abrir modal login
    bootstrap.Offcanvas.getOrCreateInstance(document.getElementById('cartOffcanvas')).hide();
    setTimeout(() => {
      new bootstrap.Modal(document.getElementById('loginModal')).show();
    }, 350);
    return;
  }

  // Validacion frontend de datos antes de enviar
  const payload = buildPayload();
  const tieneDatosInvalidos = payload.productos.some(
    p => !Number.isInteger(p.id_producto) || !Number.isInteger(p.cantidad) || p.cantidad < 1
  );
  if (tieneDatosInvalidos) {
    showToast('Hay datos inválidos en el carrito. Revisa cantidades.', 'danger');
    return;
  }

  try {
    if (btn) {
      btn.disabled = true;
      btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';
    }

    const res = await fetch(API_PEDIDOS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      if (res.status === 409) {
        showToast(data.mensaje ?? 'Stock insuficiente para uno o más productos.', 'danger');
      } else if (res.status === 401) {
        showToast(data.mensaje ?? 'Credenciales inválidas.', 'danger');
      } else {
        showToast(data.mensaje ?? 'Hubo un problema con tu pedido, inténtalo de nuevo.', 'danger');
      }
      return;
    }

    // Compra exitosa: vaciar carrito, cerrar panel, mostrar confirmacion
    clearCart();
    bootstrap.Offcanvas.getOrCreateInstance(document.getElementById('cartOffcanvas')).hide();

    const total = data.total_pedido != null ? Number(data.total_pedido).toFixed(2) : '';
    showToast(
      `¡Pedido #${data.id_pedido ?? ''} confirmado!${total ? ` Total: ${total} €` : ''} Gracias por tu compra.`,
      'success'
    );
  } catch (_) {
    showToast('Hubo un problema con tu pedido, inténtalo de nuevo.', 'danger');
  } finally {
    if (btn) {
      btn.disabled = false;
      btn.innerHTML = '<i class="bi bi-bag-check-fill me-2"></i>Finalizar compra';
    }
  }
}
