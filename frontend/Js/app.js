/**
 * TECHNOVA STORE - APLICACIÓN PRINCIPAL
 * =====================================
 * Este archivo importa y coordina todos los módulos.
 * La aplicación se inicializa automáticamente cuando el DOM está listo.
 * 
 * ESTRUCTURA DE MÓDULOS:
 * - config/     : Constantes y configuración
 * - core/       : Inicialización y funcionalidad central
 * - navigation/ : Navegación y scroll
 * - products/   : Catálogo y gestión de productos
 * - cart/       : Carrito de compras
 * - auth/       : Autenticación de usuarios
 * - ui/         : Componentes de interfaz
 * - utils/      : Utilidades y formateadores
 */

// Configuración
export * from './config/constants.js';

// Core
export * from './core/init.js';
export * from './core/theme.js';
export * from './core/animations.js';

// Navegación
export * from './navigation/scroll.js';
export * from './navigation/events.js';

// Productos
export * from './products/api.js';
export * from './products/catalog.js';
export * from './products/categories.js';
export * from './products/card.js';
export * from './products/detail.js';

// Carrito
export * from './cart/cart.js';
export * from './cart/storage.js';
export * from './cart/render.js';
export * from './cart/checkout.js';

// Autenticación
export * from './auth/login.js';
export * from './auth/register.js';
export * from './auth/session.js';
export * from './auth/ui.js';

// UI
export * from './ui/alerts.js';
export * from './ui/toast.js';
export * from './ui/status.js';
export * from './ui/viewToggle.js';

// Utilidades
export * from './utils/formatters.js';
export * from './utils/animations.js';
export * from './utils/helpers.js';