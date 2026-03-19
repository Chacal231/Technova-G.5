/**
 * CONSTANTES GLOBALES
 * ===================
 * Configuración centralizada de todas las constantes de la aplicación.
 * - API_BASE: URL base del backend Spring Boot
 * - IVA: Porcentaje de impuesto (21%)
 * - THEME_KEY: Clave para localStorage del tema
 */

export const API_BASE  = 'http://localhost:8080/api';
export const API_PRODS = `${API_BASE}/productos`;
export const API_LOGIN = `${API_BASE}/login`;
export const API_REG   = `${API_BASE}/register`;
export const API_PEDIDOS = `${API_BASE}/pedidos`;
export const IVA       = 0.21;
export const THEME_KEY = 'tn_theme';