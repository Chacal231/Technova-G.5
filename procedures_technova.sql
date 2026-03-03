USE db_technova;

-- =============================================
-- GESTIÓN DE PRODUCTOS (LISTADO Y FILTROS)
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
-- GESTIÓN DE PRODUCTOS (CRUD - SOLO ADMIN)
-- =============================================

-- Crear nuevo producto
DROP PROCEDURE IF EXISTS sp_producto_crear;
DELIMITER //
CREATE PROCEDURE sp_producto_crear(
    IN p_sku VARCHAR(10),
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    IN p_precio DECIMAL(10,2),
    IN p_stock INT,
    IN p_categoria VARCHAR(50),
    IN p_imagen VARCHAR(300)
)
BEGIN
    INSERT INTO Productos (sku, nombre, descripcion, precio, stock, categoria, imagen)
    VALUES (p_sku, p_nombre, p_descripcion, p_precio, p_stock, p_categoria, p_imagen);
END //
DELIMITER ;

-- Actualizar producto existente
DROP PROCEDURE IF EXISTS sp_producto_actualizar;
DELIMITER //
CREATE PROCEDURE sp_producto_actualizar(
    IN p_id INT,
    IN p_sku VARCHAR(10),
    IN p_nombre VARCHAR(100),
    IN p_descripcion TEXT,
    IN p_precio DECIMAL(10,2),
    IN p_stock INT,
    IN p_categoria VARCHAR(50),
    IN p_imagen VARCHAR(300)
)
BEGIN
    UPDATE Productos 
    SET sku = p_sku, 
        nombre = p_nombre, 
        descripcion = p_descripcion,
        precio = p_precio, 
        stock = p_stock, 
        categoria = p_categoria, 
        imagen = p_imagen
    WHERE id = p_id;
END //
DELIMITER ;

-- Eliminar producto (baja física)
DROP PROCEDURE IF EXISTS sp_producto_eliminar;
DELIMITER //
CREATE PROCEDURE sp_producto_eliminar(IN p_id INT)
BEGIN
    DELETE FROM Productos WHERE id = p_id;
END //
DELIMITER ;

-- Buscar producto por ID
DROP PROCEDURE IF EXISTS sp_producto_por_id;
DELIMITER //
CREATE PROCEDURE sp_producto_por_id(IN p_id INT)
BEGIN
    SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen 
    FROM Productos 
    WHERE id = p_id;
END //
DELIMITER ;

-- =============================================
-- GESTIÓN DE USUARIOS (LOGIN)
-- =============================================

-- Valida las credenciales del usuario (COMPARA HASHES)
DROP PROCEDURE IF EXISTS sp_validar_login;
DELIMITER //
CREATE PROCEDURE sp_validar_login(
    IN p_email VARCHAR(100), 
    IN p_password_hash VARCHAR(200)  -- Cambié el nombre para que sea claro
)
BEGIN
    SELECT id, email, rol 
    FROM Usuarios 
    WHERE email = p_email AND password = p_password_hash; -- Compara hash con hash
END //
DELIMITER ;

-- Obtiene usuario por email (para obtener el hash y verificarlo en Java)
DROP PROCEDURE IF EXISTS sp_obtener_usuario_por_email;
DELIMITER //
CREATE PROCEDURE sp_obtener_usuario_por_email(
    IN p_email VARCHAR(100)
)
BEGIN
    SELECT id, email, password, rol 
    FROM Usuarios 
    WHERE email = p_email;
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
    INSERT INTO Lineas_Pedido (id_pedido, id_producto, cantidad, precio_unitario_momento)
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

-- =============================================
-- PRUEBA
-- =============================================
CALL sp_obtener_usuario_por_email('admin@technova.com');

DELIMITER ;