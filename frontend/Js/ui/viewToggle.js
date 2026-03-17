/**
 * CAMBIO DE VISTA (GRID/LISTA)
 * ============================
 * Controla el toggle entre vista de cuadrícula y lista
 */

import { setListView } from '../products/catalog.js';
import { filterAndRender } from '../products/catalog.js';

export function setView(list) {
  setListView(list);
  document.getElementById('viewGrid')?.classList.toggle('active', !list);
  document.getElementById('viewList')?.classList.toggle('active',  list);
  filterAndRender();
}