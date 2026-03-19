/**
 * INDICADOR DE ESTADO DE LA API
 * =============================
 * Muestra si la API está online/offline/loading
 */

export function setApiStatus(status) {
  const dot = document.getElementById('apiStatusDot');
  const txt = document.getElementById('apiStatusText');
  
  if (dot) dot.className = `status-dot${status === 'online' ? ' online' : status === 'offline' ? ' offline' : ''}`;
  if (txt) {
    txt.textContent = status === 'online' ? 'API conectada'
                    : status === 'offline' ? 'API no disponible'
                    : 'Conectando…';
  }
}

export function showApiError(err) {
  const zone = document.getElementById('alertZone');
  if (!zone) return;
  
  const isFetch = err.message?.includes('fetch') || err.message?.includes('Network') || err.message?.includes('Failed');

  const wrapper = document.createElement('div');
  wrapper.style.cssText =
    'background:rgba(255,64,96,.06);border:1px solid rgba(255,64,96,.2);' +
    'border-radius:12px;padding:16px 20px;margin-bottom:24px;display:flex;gap:14px;align-items:flex-start;';

  const icon = document.createElement('i');
  icon.className = 'bi bi-exclamation-triangle-fill';
  icon.style.cssText = 'color:var(--danger);font-size:1.2rem;flex-shrink:0;margin-top:2px';

  const content = document.createElement('div');
  const title = document.createElement('p');
  title.style.cssText = 'font-family:var(--font-d);font-weight:700;color:var(--text);margin-bottom:4px;';
  title.textContent = isFetch ? 'No se puede conectar con la API' : `Error: ${String(err?.message ?? 'desconocido')}`;

  const subtitle = document.createElement('p');
  subtitle.style.cssText = 'font-size:13px;color:var(--text3);margin:0;';
  subtitle.textContent =
    'Verifica que el Spring Boot está corriendo en localhost:8080 y que MySQL tiene los stored procedures del SQL actualizado. Abriendo en modo demo.';

  content.appendChild(title);
  content.appendChild(subtitle);
  wrapper.appendChild(icon);
  wrapper.appendChild(content);

  zone.textContent = '';
  zone.appendChild(wrapper);
}