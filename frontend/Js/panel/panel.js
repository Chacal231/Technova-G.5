import { API_PRODS, API_PEDIDOS, API_BASE } from '../config/constants.js';

const rolesPermitidos = ['ADMIN', 'OFICINA'];
const categoriasValidas = ['Componentes', 'Periféricos', 'Redes', 'Software'];
const estadosPedido = ['Pendiente', 'Enviado', 'Entregado', 'Cancelado'];

const refs = {
  app: document.getElementById('panelApp'),
  denied: document.getElementById('panelAccessDenied'),
  roleLabel: document.getElementById('panelRoleLabel'),
  alert: document.getElementById('panelAlert'),
  tabsRoot: document.getElementById('panelTabs'),
  tabProductosBtn: document.getElementById('tabProductosBtn'),
  productsBody: document.getElementById('productsBody'),
  ordersBody: document.getElementById('ordersBody'),
  usersBody: document.getElementById('usersBody'),
  addProductBtn: document.getElementById('addProductBtn'),
  logoutBtn: document.getElementById('panelLogoutBtn'),
};

const session = {
  role: (sessionStorage.getItem('tn_role') ?? '').trim().toUpperCase(),
  user: sessionStorage.getItem('tn_user') ?? '',
};

let productsCache = [];
let usersCache = [];

function authHeaders(json = true) {
  const headers = { 'user-role': session.role };
  if (json) headers['Content-Type'] = 'application/json';
  return headers;
}

function showAlert(message, type = 'info') {
  refs.alert.className = `alert alert-${type}`;
  refs.alert.textContent = message;
  refs.alert.classList.remove('d-none');
}

function hideAlert() {
  refs.alert.classList.add('d-none');
}

async function parseErrorMessage(res, fallbackMessage) {
  const text = await res.text().catch(() => '');
  if (!text) return `${fallbackMessage} (HTTP ${res.status})`;
  try {
    const json = JSON.parse(text);
    return json.error ?? json.message ?? text;
  } catch (_) {
    return text;
  }
}

function safeText(value) {
  return String(value ?? '').replace(/[&<>"']/g, m => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;',
  })[m]);
}

function hasPanelAccess() {
  return rolesPermitidos.includes(session.role);
}

function isAdmin() {
  return session.role === 'ADMIN';
}

function setupVisibilityByRole() {
  const allowed = hasPanelAccess();
  refs.app.classList.toggle('d-none', !allowed);
  refs.denied.classList.toggle('d-none', allowed);

  if (!allowed) {
    refs.roleLabel.textContent = 'Rol actual: sin permisos';
    return;
  }

  const panelName = isAdmin() ? 'ADMINISTRACION' : 'OFICINA';
  refs.roleLabel.textContent = `Sesión de ${session.user || 'usuario'} - Rol ${panelName}`;

  if (!isAdmin()) {
    refs.tabProductosBtn?.parentElement?.classList.add('d-none');
    refs.addProductBtn?.classList.add('d-none');
    switchTab('pedidos');
  }
}

function switchTab(tabName) {
  document.querySelectorAll('#panelTabs .nav-link').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.tab === tabName);
  });
  document.querySelectorAll('.panel-section').forEach(section => {
    section.classList.toggle('d-none', section.id !== `tab-${tabName}`);
  });
}

async function loadProducts() {
  if (!isAdmin()) {
    refs.productsBody.innerHTML = '';
    return;
  }
  const res = await fetch(API_PRODS);
  if (!res.ok) throw new Error('No se pudieron cargar los productos');
  productsCache = await res.json();
  refs.productsBody.innerHTML = productsCache.map(p => `
    <tr>
      <td>${p.id}</td>
      <td>${safeText(p.sku)}</td>
      <td>${safeText(p.nombre)}</td>
      <td>${Number(p.precio).toFixed(2)} EUR</td>
      <td>${p.stock}</td>
      <td>${safeText(p.categoria)}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-warning" data-product-edit="${p.id}">Editar</button>
        <button class="btn btn-sm btn-info text-white" data-product-stock="${p.id}">Añadir stock</button>
        <button class="btn btn-sm btn-danger" data-product-delete="${p.id}">Eliminar</button>
      </td>
    </tr>
  `).join('');
}

function askProductData(initial = {}) {
  const sku = prompt('SKU:', initial.sku ?? '');
  if (sku === null) return null;
  const nombre = prompt('Nombre:', initial.nombre ?? '');
  if (nombre === null) return null;
  const descripcion = prompt('Descripción:', initial.descripcion ?? '');
  if (descripcion === null) return null;
  const precioTxt = prompt('Precio:', initial.precio ?? '0');
  if (precioTxt === null) return null;
  const stockTxt = prompt('Stock:', initial.stock ?? '0');
  if (stockTxt === null) return null;
  const categoria = prompt(`Categoría (${categoriasValidas.join(', ')}):`, initial.categoria ?? 'Componentes');
  if (categoria === null) return null;
  const imagen = prompt('URL imagen:', initial.imagen ?? '');
  if (imagen === null) return null;

  const precio = Number(precioTxt);
  const stock = Number(stockTxt);
  const categoriaNormalizada = categoria.trim();
  if (!sku.trim() || !nombre.trim() || Number.isNaN(precio) || Number.isNaN(stock)) {
    showAlert('Datos de producto incompletos o inválidos.', 'warning');
    return null;
  }
  if (!categoriasValidas.includes(categoriaNormalizada)) {
    showAlert(`Categoría inválida. Usa: ${categoriasValidas.join(', ')}`, 'warning');
    return null;
  }

  return {
    sku: sku.trim(),
    nombre: nombre.trim(),
    descripcion: descripcion.trim(),
    precio,
    stock,
    categoria: categoriaNormalizada,
    imagen: imagen.trim(),
  };
}

async function createProduct() {
  const payload = askProductData();
  if (!payload) return;
  const res = await fetch(API_PRODS, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? 'No se pudo crear el producto');
  }
  showAlert('Producto creado correctamente.', 'success');
  await loadProducts();
}

async function editProduct(id) {
  const original = productsCache.find(p => Number(p.id) === id);
  if (!original) return;
  const current = {
    sku: original.sku,
    nombre: original.nombre,
    precio: original.precio,
    stock: original.stock,
    categoria: original.categoria,
    descripcion: original.descripcion ?? '',
    imagen: original.imagen ?? '',
  };
  const payload = askProductData(current);
  if (!payload) return;

  const res = await fetch(`${API_PRODS}/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? 'No se pudo editar el producto');
  }
  showAlert('Producto actualizado.', 'success');
  await loadProducts();
}

async function addStock(id) {
  const original = productsCache.find(p => Number(p.id) === id);
  if (!original) return;
  const actual = Number(original.stock ?? 0);
  const extra = Number(prompt('Cantidad de stock a añadir:', '1'));
  if (!Number.isFinite(extra) || extra <= 0) return;

  const payload = {
    sku: original.sku ?? '',
    nombre: original.nombre ?? '',
    descripcion: original.descripcion ?? '',
    precio: Number(original.precio ?? 0),
    stock: actual + extra,
    categoria: original.categoria ?? 'Componentes',
    imagen: original.imagen ?? '',
  };

  const res = await fetch(`${API_PRODS}/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? 'No se pudo actualizar el stock');
  }
  showAlert('Stock actualizado.', 'success');
  await loadProducts();
}

async function deleteProduct(id) {
  if (!confirm(`¿Eliminar producto #${id}?`)) return;
  const res = await fetch(`${API_PRODS}/${id}`, {
    method: 'DELETE',
    headers: authHeaders(false),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? 'No se pudo eliminar el producto');
  }
  showAlert('Producto eliminado.', 'success');
  await loadProducts();
}

async function loadOrders() {
  const ordersUrl = `${API_PEDIDOS}?t=${Date.now()}`;
  const res = await fetch(ordersUrl, {
    headers: authHeaders(false),
    cache: 'no-store',
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? `No se pudieron cargar los pedidos (HTTP ${res.status})`);
  }
  const data = await res.json();
  refs.ordersBody.innerHTML = data.map(o => `
    <tr>
      <td>${o.id}</td>
      <td>${safeText(o.fecha)}</td>
      <td><span class="badge text-bg-secondary">${safeText(o.estado)}</span></td>
      <td>${Number(o.total_pedido).toFixed(2)} EUR</td>
      <td>${o.id_usuario}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-warning" data-order-status="${o.id}">Cambiar estado</button>
        <button class="btn btn-sm btn-danger" data-order-delete="${o.id}">Eliminar</button>
      </td>
    </tr>
  `).join('');
}

async function changeOrderStatus(id) {
  const estado = prompt(
    `Nuevo estado:\n1) Pendiente\n2) Enviado\n3) Entregado\n4) Cancelado\n\nTambién puedes escribir el nombre del estado.`,
    'Pendiente'
  );
  if (!estado) return;
  const estadoNormalizado = estado.trim();

  let estadoCanonico = null;
  if (/^[1-4]$/.test(estadoNormalizado)) {
    estadoCanonico = estadosPedido[Number(estadoNormalizado) - 1];
  } else {
    estadoCanonico = estadosPedido.find(
      e => e.toLowerCase() === estadoNormalizado.toLowerCase()
    ) ?? null;
  }

  if (!estadoCanonico) {
    showAlert(`Estado inválido. Usa: ${estadosPedido.join(', ')}`, 'warning');
    return;
  }
  const candidatos = [
    { url: `${API_PEDIDOS}/${id}/estado`, method: 'PUT' },
    { url: `${API_PEDIDOS}/${id}`, method: 'PUT' },
    { url: `${API_PEDIDOS}/estado/${id}`, method: 'PUT' },
    { url: `${API_PEDIDOS}/${id}/estado`, method: 'PATCH' },
  ];

  let lastError = 'No se pudo cambiar el estado';
  let actualizado = false;

  for (const intento of candidatos) {
    const res = await fetch(intento.url, {
      method: intento.method,
      headers: authHeaders(),
      body: JSON.stringify({ estado: estadoCanonico }),
    });

    if (res.ok) {
      actualizado = true;
      break;
    }

    // Si no existe la ruta, probamos la siguiente.
    if (res.status === 404) {
      lastError = await parseErrorMessage(res, 'Endpoint no encontrado');
      continue;
    }

    lastError = await parseErrorMessage(res, 'No se pudo cambiar el estado');
    break;
  }

  if (!actualizado) {
    throw new Error(lastError);
  }

  showAlert(`Estado del pedido actualizado a "${estadoCanonico}".`, 'success');
  await loadOrders();
}

async function deleteOrder(id) {
  if (!confirm(`¿Eliminar pedido #${id}?`)) return;
  const res = await fetch(`${API_PEDIDOS}/${id}`, {
    method: 'DELETE',
    headers: authHeaders(false),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? 'No se pudo eliminar el pedido');
  }
  showAlert('Pedido eliminado.', 'success');
  await loadOrders();
}

async function loadUsers() {
  const usersUrl = `${API_BASE}/usuarios?t=${Date.now()}`;
  const res = await fetch(usersUrl, {
    headers: authHeaders(false),
    cache: 'no-store',
  });
  if (!res.ok) {
    throw new Error(await parseErrorMessage(res, 'No se pudieron cargar los usuarios'));
  }
  const users = await res.json();
  usersCache = users;
  if (!Array.isArray(usersCache) || usersCache.length === 0) {
    refs.usersBody.innerHTML = `
      <tr>
        <td colspan="5" class="text-center text-muted py-3">No hay usuarios para mostrar.</td>
      </tr>
    `;
    return;
  }
  refs.usersBody.innerHTML = usersCache.map(u => `
    <tr>
      <td>${u.id}</td>
      <td>${safeText(u.email)}</td>
      <td>${safeText(u.nombre)}</td>
      <td>${safeText(u.rol)}</td>
      <td class="text-end">
        ${isAdmin() ? `
          <button class="btn btn-sm btn-warning" data-user-edit="${u.id}">Editar</button>
          <button class="btn btn-sm btn-danger" data-user-delete="${u.id}">Eliminar</button>
        ` : '<span class="text-muted">Solo lectura</span>'}
      </td>
    </tr>
  `).join('');
}

async function editUser(id) {
  const original = usersCache.find(u => Number(u.id) === id);
  if (!original) return;
  const email = prompt('Email:', original.email ?? '');
  if (email === null) return;
  const nombre = prompt('Nombre:', original.nombre ?? '');
  if (nombre === null) return;
  const rol = prompt('Rol (ADMIN, OFICINA, CLIENTE):', original.rol ?? 'CLIENTE');
  if (rol === null) return;

  const res = await fetch(`${API_BASE}/usuarios/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify({ email, nombre, rol }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? 'No se pudo editar el usuario');
  }
  showAlert('Usuario actualizado.', 'success');
  await loadUsers();
}

async function deleteUser(id) {
  if (!confirm(`¿Eliminar usuario #${id}?`)) return;
  const res = await fetch(`${API_BASE}/usuarios/${id}`, {
    method: 'DELETE',
    headers: authHeaders(false),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error ?? 'No se pudo eliminar el usuario');
  }
  showAlert('Usuario eliminado.', 'success');
  await loadUsers();
}

function bindEvents() {
  refs.tabsRoot?.addEventListener('click', e => {
    const btn = e.target.closest('[data-tab]');
    if (!btn) return;
    switchTab(btn.dataset.tab);
    hideAlert();
  });

  refs.addProductBtn?.addEventListener('click', async () => {
    try {
      await createProduct();
    } catch (err) {
      showAlert(err.message, 'danger');
    }
  });

  refs.productsBody?.addEventListener('click', async e => {
    const edit = e.target.closest('[data-product-edit]');
    const stock = e.target.closest('[data-product-stock]');
    const del = e.target.closest('[data-product-delete]');
    try {
      if (edit) await editProduct(Number(edit.dataset.productEdit));
      if (stock) await addStock(Number(stock.dataset.productStock));
      if (del) await deleteProduct(Number(del.dataset.productDelete));
    } catch (err) {
      showAlert(err.message, 'danger');
    }
  });

  refs.ordersBody?.addEventListener('click', async e => {
    const status = e.target.closest('[data-order-status]');
    const del = e.target.closest('[data-order-delete]');
    try {
      if (status) await changeOrderStatus(Number(status.dataset.orderStatus));
      if (del) await deleteOrder(Number(del.dataset.orderDelete));
    } catch (err) {
      showAlert(err.message, 'danger');
    }
  });

  refs.usersBody?.addEventListener('click', async e => {
    const edit = e.target.closest('[data-user-edit]');
    const del = e.target.closest('[data-user-delete]');
    try {
      if (edit) await editUser(Number(edit.dataset.userEdit));
      if (del) await deleteUser(Number(del.dataset.userDelete));
    } catch (err) {
      showAlert(err.message, 'danger');
    }
  });

  refs.logoutBtn?.addEventListener('click', () => {
    ['tn_user', 'tn_role', 'tn_user_id', 'tn_token'].forEach(k => sessionStorage.removeItem(k));
    window.location.href = 'index.html';
  });
}

async function init() {
  setupVisibilityByRole();
  bindEvents();
  if (!hasPanelAccess()) return;
  try {
    await loadOrders();
    await loadProducts();
    await loadUsers();
  } catch (err) {
    showAlert(err.message, 'danger');
  }
}

init();
