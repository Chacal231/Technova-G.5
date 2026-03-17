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
  
  zone.innerHTML = `
    <div style="background:rgba(255,64,96,.06);border:1px solid rgba(255,64,96,.2);
                border-radius:12px;padding:16px 20px;margin-bottom:24px;display:flex;gap:14px;align-items:flex-start;">
      <i class="bi bi-exclamation-triangle-fill" style="color:var(--danger);font-size:1.2rem;flex-shrink:0;margin-top:2px"></i>
      <div>
        <p style="font-family:var(--font-d);font-weight:700;color:var(--text);margin-bottom:4px;">
          ${isFetch ? 'No se puede conectar con la API' : `Error: ${err.message}`}
        </p>
        <p style="font-size:13px;color:var(--text3);margin:0;">
          Verifica que el Spring Boot está corriendo en
          <code style="background:var(--surface2);padding:1px 5px;border-radius:4px">localhost:8080</code>
          y que MySQL tiene los stored procedures del SQL actualizado.
          Abriendo en modo demo.
        </p>
      </div>
    </div>
  `;
}