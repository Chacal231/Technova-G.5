/**
 * API DE PRODUCTOS
 * ================
 * Comunicación con el backend para obtener productos.
 * Incluye modo demo cuando la API no está disponible.
 */

import { API_PRODS } from '../config/constants.js';
import { filterAndRender } from './catalog.js';
import { populateCategories } from './categories.js';
import { setApiStatus, showApiError } from '../ui/status.js';
import { showToast } from '../ui/toast.js';
import { animateNumber } from '../utils/animations.js';

export let allProducts = [];

export async function loadProducts() {
  setApiStatus('loading');
  try {
    const res = await fetch(API_PRODS);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    allProducts = Array.isArray(data)
      ? data
      : (data.data || data.productos || data.content || []);
    allProducts = attachLocalImages(allProducts);
    setApiStatus('online');
    populateCategories(allProducts);
    filterAndRender();
    
    const el = document.getElementById('statProducts');
    if (el) animateNumber(el, 0, allProducts.length, 800);
  } catch (err) {
    console.warn('[TechNova] API no disponible, modo demo.', err);
    setApiStatus('offline');
    allProducts = attachLocalImages(getDemoProducts());
    populateCategories(allProducts);
    filterAndRender();
    showToast('API no disponible — mostrando catálogo de demo', 'warning');
    showApiError(err);
  }
}

export function getDemoProducts() {
  return []; // Tus productos demo aquí
}

export function attachLocalImages(products) {
  const IMAGE_MAP_NAME = {
    'pccom ready amd ryzen 7': 'assets/assets_ready_amd_ryzen_7.png-removebg-preview.png',
    'geforce rtx 4070': 'assets/rtx_4070.png',
    'monitor msi 27" 144hz': 'assets/monitor_msi_27_144hz.png',
    'intel core i5-12400f': 'assets/intel_core_i5_12400f.png',
    'logitech g502 hero': 'assets/logitech_g502_hero.png',
    'cable ethernet cat6 10m': 'assets/cable_ethernet_cat6_10m.png',
    'router asus rt-ax58u': 'assets/router_asus_rt_ax58u.png',
    'windows 11 home': 'assets/windows_11_home.png',
    'microsoft 365 personal': 'assets/microsoft_365_personal.png',
    'corsair k70 rgb': 'assets/corsair_k70_rgb.png',
  };

  const IMAGE_MAP_SKU = {
    'pc-ryz-580': 'assets/assets_ready_amd_ryzen_7.png-removebg-preview.png',
    'gpu-rtx-407': 'assets/rtx_4070.png',
    'mon-msi-27': 'assets/monitor_msi_27_144hz.png',
    'cpu-int-65': 'assets/intel_core_i5_12400f.png',
    'rat-log-g5': 'assets/logitech_g502_hero.png',
    'cab-eth-10': 'assets/cable_ethernet_cat6_10m.png',
    'rou-asus-ax': 'assets/router_asus_rt_ax58u.png',
    'so-win-11': 'assets/windows_11_home.png',
    'off-365-pe': 'assets/microsoft_365_personal.png',
    'tec-cor-mx': 'assets/corsair_k70_rgb.png',
  };

  return (products || []).map(p => {
    const name = (p.nombre || p.name || '').toLowerCase().trim();
    const sku  = (p.sku || '').toLowerCase().trim();

    const localPath =
      IMAGE_MAP_NAME[name] ||
      IMAGE_MAP_SKU[sku]   ||
      null;

    return localPath ? { ...p, imagen: localPath } : p;
  });
}