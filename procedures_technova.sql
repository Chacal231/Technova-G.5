USE db_technova;

-- =============================================
-- GESTIÓN DE PRODUCTOS
-- =============================================

-- Obtiene todos los campos de la tabla productos para mostrar el catálogo
DROP PROCEDURE IF EXISTS sp_productos_listar;
DELIMITER //
CREATE PROCEDURE sp_productos_listar()
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos;
END //
DELIMITER ;

-- Filtra los productos según la categoría especificada por parámetro
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
-- GESTIÓN DE USUARIOS
-- =============================================

-- Comprueba la existencia del usuario y valida sus credenciales de acceso
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
-- GESTIÓN DE PEDIDOS
-- =============================================

-- Recupera el listado completo de pedidos realizados en la tienda
DROP PROCEDURE IF EXISTS sp_pedidos_listar;
DELIMITER //
CREATE PROCEDURE sp_pedidos_listar()
BEGIN
    SELECT id, id_usuario, fecha, total_pedido, estado 
    FROM Pedidos;
END //
DELIMITER ;

-- Registra la cabecera del pedido y devuelve el ID autogenerado para vincular sus líneas
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

-- Inserta el detalle de cada producto asociado a un número de pedido específico
DROP PROCEDURE IF EXISTS sp_crear_linea_pedido;
DELIMITER //
CREATE PROCEDURE sp_crear_linea_pedido(
    IN p_id_pedido INT,
    IN p_id_producto INT,
    IN p_cantidad INT,
    IN p_precio DECIMAL(10,2)
)
BEGIN
    INSERT INTO Lineas_Pedido (id_pedido, id_producto, cantidad, precio_unitario)
    VALUES (p_id_pedido, p_id_producto, p_cantidad, p_precio);
END //
DELIMITER ;

-- Reduce la cantidad de stock disponible de un producto tras confirmarse su venta
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

