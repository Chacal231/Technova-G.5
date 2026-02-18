USE db_technova;

-- =============================================
-- BLOQUE 1: LECTURA DE DATOS (GET)
-- =============================================

-- 1. Listar TODOS los productos (Obligatorio Tarea 3.2)
-- Devuelve el catálogo completo.
DROP PROCEDURE IF EXISTS sp_productos_listar;
DELIMITER //
CREATE PROCEDURE sp_productos_listar()
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos;
END //
DELIMITER ;

-- 2. Listar productos POR CATEGORÍA (Obligatorio Tarea 3.2)
-- Filtra para cuando el usuario pincha en "Monitores", "Componentes", etc.
DROP PROCEDURE IF EXISTS sp_productos_por_categoria;
DELIMITER //
CREATE PROCEDURE sp_productos_por_categoria(IN p_categoria VARCHAR(100))
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos
    WHERE categoria = p_categoria;
END //
DELIMITER ;

-- 3. Listar productos POR ID (Extra útil)
-- Necesario si queréis ver el detalle de un solo producto.
DROP PROCEDURE IF EXISTS sp_producto_por_id;
DELIMITER //
CREATE PROCEDURE sp_producto_por_id(IN p_id INT)
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos
    WHERE id = p_id;
END //
DELIMITER ;

-- 4. Listar Pedidos con FILTROS (Obligatorio Tarea 3.2)
-- Filtra por estado y rango de fechas.
-- TRUCO: Si p_estado es NULL, devuelve todos los estados.
DROP PROCEDURE IF EXISTS sp_pedidos_listar;
DELIMITER //
CREATE PROCEDURE sp_pedidos_listar(
    IN p_estado VARCHAR(30), 
    IN p_fecha_ini DATE, 
    IN p_fecha_fin DATE
)
BEGIN
    SELECT id, id_usuario, fecha, total_pedido, estado
    FROM Pedidos
    WHERE (estado = p_estado OR p_estado IS NULL) 
      AND (DATE(fecha) BETWEEN p_fecha_ini AND p_fecha_fin);
END //
DELIMITER ;

-- =============================================
-- BLOQUE 2: LÓGICA DE NEGOCIO Y ESCRITURA (POST)
-- =============================================

-- 5. Validar Login (Para Tarea 3.4 - POST /login)
-- Comprueba si existe el email y pass, y devuelve el rol.
DROP PROCEDURE IF EXISTS sp_validar_login;
DELIMITER //
CREATE PROCEDURE sp_validar_login(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(200)
)
BEGIN
    SELECT id, email, rol 
    FROM Usuarios 
    WHERE email = p_email AND password = p_password;
END //
DELIMITER ;

-- 6. Crear Cabecera de Pedido (Para Tarea 3.4 - POST /pedidos)
-- Inserta el pedido y devuelve el ID generado (IMPORTANTE para las líneas).
DROP PROCEDURE IF EXISTS sp_crear_pedido;
DELIMITER //
CREATE PROCEDURE sp_crear_pedido(
    IN p_id_usuario INT,
    IN p_total_pedido DECIMAL(10,2),
    OUT p_nuevo_id INT
)
BEGIN
    -- Insertamos el pedido con fecha actual (NOW()) y estado 'Pendiente'
    INSERT INTO Pedidos (id_usuario, fecha, total_pedido, estado) 
    VALUES (p_id_usuario, NOW(), p_total_pedido, 'Pendiente');
    
    -- Devolvemos el ID que se acaba de crear
    SET p_nuevo_id = LAST_INSERT_ID();
    SELECT p_nuevo_id as id_generado; 
END //
DELIMITER ;

-- 7. Insertar Línea de Pedido (Para Tarea 3.4 - POST /pedidos)
-- Se llama varias veces, una por cada producto del carrito.
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

-- 8. Actualizar Stock (Opcional para nota)
-- Resta la cantidad comprada del stock del producto.
DROP PROCEDURE IF EXISTS sp_actualizar_stock;
DELIMITER //
CREATE PROCEDURE sp_actualizar_stock(
    IN p_id_producto INT,
    IN p_cantidad INT
)
BEGIN
    UPDATE Productos 
    SET stock = stock - p_cantidad 
    WHERE id = p_id_producto;
END //
DELIMITER ;
