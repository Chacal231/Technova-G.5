-- 1º Creación de la base de datos, usando DROP DATABASE IF EXISTS para poder reiniciar pruebas
DROP DATABASE IF EXISTS db_technova;
CREATE DATABASE db_technova CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_technova;

-- 2º Creación de tablas con los tipos de datos correctos

-- Tabla Usuarios
CREATE TABLE Usuarios (
id INT AUTO_INCREMENT PRIMARY KEY,
email VARCHAR (100) NOT NULL UNIQUE,
password VARCHAR (200) NOT NULL,
rol ENUM ('CLIENTE', 'OFICINA', 'ADMIN') NOT NULL
);

 -- Tabla Productos
CREATE TABLE Productos (
id INT AUTO_INCREMENT PRIMARY KEY,
sku VARCHAR(10) NOT NULL UNIQUE,
nombre VARCHAR(100) NOT NULL,
descripcion TEXT,
precio DECIMAL (10, 2) NOT NULL CHECK (precio >= 0), 
stock int NOT NULL CHECK (stock >= 0), 
categoria ENUM ('Componentes', 'Periféricos', 'Redes', 'Software') NOT NULL,
imagen VARCHAR (300)                
);

-- Tabla Pedidos
CREATE TABLE Pedidos (
id INT AUTO_INCREMENT PRIMARY KEY,
id_usuario INT NOT NULL,
fecha DATETIME NOT NULL,
total_pedido DECIMAL(10, 2) NOT NULL,
estado ENUM ('Pendiente', 'Enviado', 'Entregado', 'Cancelado'),

-- Relación con la tabla Usuarios
CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) 
REFERENCES Usuarios(id) 

);

-- Tabla Lineas_Pedido
CREATE TABLE Lineas_Pedido (
id INT AUTO_INCREMENT NOT NULL,
id_pedido INT NOT NULL,
id_producto INT NOT NULL,
cantidad INT NOT NULL CHECK (cantidad > 0),
precio_unitario_momento DECIMAL(10, 2) NOT NULL, 
PRIMARY KEY (id, id_pedido, id_producto),

-- Relaciones con tabla Pedidos y Productos
CONSTRAINT fk_linea_pedido FOREIGN KEY (id_pedido) 
REFERENCES Pedidos(id),
CONSTRAINT fk_linea_producto FOREIGN KEY (id_producto) 
REFERENCES Productos(id) 
);

-- 3º Insertar datos 

-- Datos Usuarios
INSERT INTO Usuarios (email, password, rol) VALUES 
('admin@technova.com', 'admin123', 'ADMIN'),
('oficina@technova.com', 'oficina123', 'OFICINA'),
('cliente@gmail.com', 'cliente123', 'CLIENTE');

-- Datos Productos (los datos son basados en los bocetos que hemos utilizado)
INSERT INTO Productos (sku, nombre, descripcion, precio, stock, categoria, imagen) VALUES 
('PC-RYZ-580', 'PcCom Ready AMD Ryzen 7', 'Ryzen 7 5800X, 32GB RAM, RTX 4060', 1390.00, 5, 'Componentes', 'img/pc-ryzen.jpg'),
('GPU-RTX-40', 'GeForce RTX 4070', 'Tarjeta gráfica 12GB GDDR6X', 599.90, 10, 'Componentes', 'img/rtx4070.jpg'),
('MON-MSI-27', 'Monitor MSI 27" 144Hz', 'Monitor Curvo Gaming Full HD', 229.50, 15, 'Periféricos', 'img/mon-msi.jpg'),
('CPU-INT-I5', 'Intel Core i5-12400F', 'Procesador 12ª Gen LGA1700', 149.00, 20, 'Componentes', 'img/i5-12400.jpg'),
('RAT-LOG-G5', 'Logitech G502 HERO', 'Ratón Gaming RGB 25K DPI', 45.00, 50, 'Periféricos', 'img/logitech-g5.jpg'),
('CAB-ETH-10', 'Cable Ethernet Cat6 10m', 'Cable de red alta velocidad', 12.50, 100, 'Redes', 'img/cable-eth.jpg'),
('ROU-ASU-AX', 'Router ASUS RT-AX58U', 'Router WiFi 6 Doble Banda', 119.99, 8, 'Redes', 'img/router-asus.jpg'),
('SO-WIN-11', 'Windows 11 Home', 'Licencia Digital OEM 64bits', 115.00, 200, 'Software', 'img/win11.jpg'),
('OFF-365-PE', 'Microsoft 365 Personal', 'Suscripción 1 año', 69.00, 100, 'Software', 'img/office.jpg'),
('TEC-COR-K7', 'Corsair K70 RGB', 'Teclado Mecánico Cherry MX', 159.90, 12, 'Periféricos', 'img/corsair-k70.jpg');

-- Pedidos y Lineas_Pedido
INSERT INTO Pedidos (id_usuario, fecha, total_pedido, estado) VALUES 
(3, '2026-02-06 12:00:00', 1435.00, 'Enviado');
INSERT INTO Lineas_Pedido (id, id_pedido, id_producto, cantidad, precio_unitario_momento) VALUES 
(1, 1, 1, 1, 1390.00),
(2, 1, 5, 1, 45.00);

INSERT INTO Pedidos (id_usuario, fecha, total_pedido, estado) VALUES 
(3, '2026-02-06 18:00:00', 115.00, 'Pendiente');
INSERT INTO Lineas_Pedido (id, id_pedido, id_producto, cantidad, precio_unitario_momento) VALUES 
(3, 2, 8, 1, 115.00);

SELECT * FROM usuarios;
