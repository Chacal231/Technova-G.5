USE db_technova;

-- 1. Procedimiento para obtener todos los productos 
DROP PROCEDURE IF EXISTS sp_listar_productos;
DELIMITER //
CREATE PROCEDURE sp_listar_productos()
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos;
END //
DELIMITER ;

-- 2. Procedimiento para obtener todos los pedidos 
DROP PROCEDURE IF EXISTS sp_listar_pedidos;
DELIMITER //
CREATE PROCEDURE sp_listar_pedidos()
BEGIN
    SELECT id, id_usuario, fecha, total_pedido, estado 
    FROM Pedidos;
END //
DELIMITER ;

-- 3. Procedimiento para crear un nuevo usuario 
DROP PROCEDURE IF EXISTS sp_crear_usuario;
DELIMITER //
CREATE PROCEDURE sp_crear_usuario(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(200),
    IN p_rol ENUM('CLIENTE', 'OFICINA', 'ADMIN')
)
BEGIN
    INSERT INTO Usuarios (email, password, rol) 
    VALUES (p_email, p_password, p_rol);
END //
DELIMITER ;

-- 4. Procedimiento para registrar un pedido 
-- Nota: Este solo crea la cabecera del pedido
DROP PROCEDURE IF EXISTS sp_crear_pedido;
DELIMITER //
CREATE PROCEDURE sp_crear_pedido(
    IN p_id_usuario INT,
    IN p_fecha DATETIME,
    IN p_total_pedido DECIMAL(10,2),
    IN p_estado ENUM('Pendiente', 'Enviado', 'Entregado', 'Cancelado')
)
BEGIN
    INSERT INTO Pedidos (id_usuario, fecha, total_pedido, estado) 
    VALUES (p_id_usuario, p_fecha, p_total_pedido, p_estado);
END //
DELIMITER ;
