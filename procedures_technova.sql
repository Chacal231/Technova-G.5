-- ============================================================
--  procederes_technova.sql — STORED PROCEDURES
--  Ejecutar DESPUÉS de db_technova.sql
-- ============================================================
USE db_technova;
DELIMITER $$
-- ============================================================
--  STORED PROCEDURES — Productos
-- ============================================================

-- Lista todos los productos
CREATE PROCEDURE sp_productos_listar()
BEGIN
SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen
FROM Productos
ORDER BY id;
END$$

-- Lista productos por categoría
CREATE PROCEDURE sp_productos_por_categoria(IN p_categoria VARCHAR(50))
BEGIN
SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen
FROM Productos
WHERE categoria = p_categoria
ORDER BY id;
END$$

-- Busca un producto por ID
CREATE PROCEDURE sp_producto_por_id(IN p_id INT)
BEGIN
SELECT id, sku, nombre, descripcion, precio, stock, categoria, imagen
FROM Productos
WHERE id = p_id;
END$$

-- Crea un producto nuevo (7 parámetros)
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
END$$

-- Actualiza un producto (8 parámetros: id + los 7 de crear)
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
END$$

-- Elimina un producto
CREATE PROCEDURE sp_producto_eliminar(IN p_id INT)
BEGIN
DELETE FROM Productos WHERE id = p_id;
END$$

-- Actualiza el stock de un producto (resta la cantidad vendida)
CREATE PROCEDURE sp_actualizar_stock(
  IN p_id_producto INT,
  IN p_cantidad    INT
)
BEGIN
  UPDATE Productos
  SET stock = stock - p_cantidad
  WHERE id = p_id_producto AND stock >= p_cantidad;
END$$


-- ============================================================
--  STORED PROCEDURES — Usuarios
-- ============================================================

-- Obtiene un usuario por email (devuelve id, nombre, email, password hash y rol)
CREATE PROCEDURE sp_obtener_usuario_por_email(IN p_email VARCHAR(100))
BEGIN
SELECT id, nombre, email, password, rol
FROM Usuarios
WHERE email = p_email
LIMIT 1;
END$$

-- Valida el login y devuelve datos del usuario (sin password)
CREATE PROCEDURE sp_validar_login(
  IN p_email         VARCHAR(100),
  IN p_password_hash VARCHAR(200)
)
BEGIN
  SELECT id, nombre, email, rol
  FROM Usuarios
  WHERE email = p_email
    AND password = p_password_hash
  LIMIT 1;
END$$


-- ============================================================
--  STORED PROCEDURES — Pedidos
-- ============================================================

-- Lista todos los pedidos
CREATE PROCEDURE sp_pedidos_listar()
BEGIN
  SELECT p.id, p.id_usuario, p.fecha, p.total_pedido, p.estado,
         u.email AS email_usuario
  FROM Pedidos p
  JOIN Usuarios u ON u.id = p.id_usuario
  ORDER BY p.fecha DESC;
END$$

-- Crea un pedido nuevo (3 parámetros; la fecha se pone automáticamente)
CREATE PROCEDURE sp_crear_pedido(
  IN p_id_usuario   INT,
  IN p_total_pedido DECIMAL(10,2),
  IN p_estado       VARCHAR(20)
)
BEGIN
  INSERT INTO Pedidos (id_usuario, fecha, total_pedido, estado)
  VALUES (p_id_usuario, NOW(), p_total_pedido, p_estado);
  -- Devuelve el ID del pedido recién creado
  SELECT LAST_INSERT_ID() AS id_pedido;
END$$

-- Crea una línea de pedido
CREATE PROCEDURE sp_crear_linea_pedido(
  IN p_id_pedido               INT,
  IN p_id_producto             INT,
  IN p_cantidad                INT,
  IN p_precio_unitario_momento DECIMAL(10,2)
)
BEGIN
  DECLARE v_max_id INT DEFAULT 0;
  SELECT COALESCE(MAX(id), 0) + 1 INTO v_max_id
  FROM Lineas_Pedido
  WHERE id_pedido = p_id_pedido;

  INSERT INTO Lineas_Pedido (id, id_pedido, id_producto, cantidad, precio_unitario_momento)
  VALUES (v_max_id, p_id_pedido, p_id_producto, p_cantidad, p_precio_unitario_momento);
END$$

DELIMITER ;