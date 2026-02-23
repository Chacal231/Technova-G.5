USE db_technova;

-- =============================================
-- 1. PRODUCTOS (Requisito Tarea 3.3)
-- =============================================

-- Listar catálogo completo
DROP PROCEDURE IF EXISTS sp_productos_listar;
DELIMITER //
CREATE PROCEDURE sp_productos_listar()
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos;
END //
DELIMITER ;

-- Filtrar por categoría (ej: 'Componentes')
DROP PROCEDURE IF EXISTS sp_productos_por_categoria;
DELIMITER //
CREATE PROCEDURE sp_productos_por_categoria(IN p_categoria VARCHAR(100))
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos
    WHERE categoria = p_categoria;
END //
DELIMITER ;


-- =============================================
-- 2. USUARIOS / LOGIN (Necesario para Tarea 3.4)
-- =============================================

-- Validar credenciales y devolver rol
DROP PROCEDURE IF EXISTS sp_validar_login;
DELIMITER //
CREATE PROCEDURE sp_validar_login(IN p_email VARCHAR(100), IN p_password VARCHAR(200))
BEGIN
    SELECT id, email, rol 
    FROM Usuarios 
    WHERE email = p_email AND password = p_password;
END //
DELIMITER ;


-- =============================================
-- 3. PEDIDOS (Requisito Tarea 3.4)
-- =============================================

-- Listar todos los pedidos (para vista de admin)
DROP PROCEDURE IF EXISTS sp_pedidos_listar;
DELIMITER //
CREATE PROCEDURE sp_pedidos_listar()
BEGIN
    SELECT id, id_usuario, fecha, total_pedido, estado 
    FROM Pedidos;
END //
DELIMITER ;

-- Crear la cabecera del pedido y obtener el ID generado
DROP PROCEDURE IF EXISTS sp_crear_pedido;
DELIMITER //
CREATE PROCEDURE sp_crear_pedido(
    IN p_id_usuario INT,
    IN p_total DECIMAL(10,2),
    OUT p_nuevo_id INT
)
BEGIN
    INSERT INTO Pedidos (id_usuario, fecha, total_pedido, estado) 
    VALUES (p_id_usuario, NOW(), p_total, 'Pendiente');
    
    SET p_nuevo_id = LAST_INSERT_ID();
END //
DELIMITER ;

-- Insertar cada línea del pedido (los productos del carrito)
DROP PROCEDURE IF EXISTS sp_crear_linea_pedido;
DELIMITER //
CREATE PROCEDURE sp_crear_linea_pedido(
    IN p_id_pedido INT,
    IN p_id_producto INT,
    IN p_cantidad INT,
    IN p_precio DECIMAL(10,2)
)
BEGIN
    INSERT INTO Lineas_Pedido (id_pedido, id_producto, cantidad, precio_unitario_momento)
    VALUES (p_id_pedido, p_id_producto, p_cantidad, p_precio);
END //
DELIMITER ;

-- EXTRA PARA NOTA: Actualizar stock tras una venta
DROP PROCEDURE IF EXISTS sp_actualizar_stock;
DELIMITER //
CREATE PROCEDURE sp_actualizar_stock(
    IN p_id_producto INT,
    IN p_cantidad_comprada INT
)
BEGIN
    UPDATE Productos 
    SET stock = stock - p_cantidad_comprada 
    WHERE id = p_id_producto;
END //
DELIMITER ;

