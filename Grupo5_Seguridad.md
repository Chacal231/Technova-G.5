# Grupo 5 - Documento de Seguridad (Entregable 6)

## 1) Tabla de Validaciones

| Campo | Validacion Frontend | Validacion Backend (API) |
|---|---|---|
| Email | `type="email"` en login y registro + regex en JS (`/^\S+@\S+\.\S+$/`) | Validacion con regex en `/api/register`; verificacion de `id_usuario` existente en `/api/pedidos` |
| Cantidad | Control en carrito: no permite superar el stock disponible (`qty >= 1`, limite maximo = stock) | Rechazo si `cantidad < 1` o no es entero (`400 Bad Request`) |
| Stock | Limite visual: no deja anadir mas unidades que las disponibles | Consulta `SELECT stock FROM Productos WHERE id = ? FOR UPDATE` antes de insertar. Si no hay stock -> `409 Conflict` |
| Productos | Solo se envian `id_producto` y `cantidad`, nunca el precio | Rechazo si lista vacia, campos nulos o formato invalido (`400 Bad Request`) |
| Precio / Total | No se envia desde frontend | Precio real leido de tabla `Productos` en BD y total calculado en servidor |

## 2) Catalogo de Errores Controlados

| Situacion | Codigo HTTP devuelto | Mensaje JSON | Comportamiento Visual (Web) |
|---|---:|---|---|
| Login incorrecto | 401 | `"Credenciales incorrectas"` | Borde rojo en inputs + mensaje de error en formulario |
| Stock insuficiente | 409 | `"Stock insuficiente"` | Toast de error impidiendo compra |
| Datos de checkout invalidos | 400 | `"La lista de productos no puede estar vacia"` / `"La cantidad debe ser un entero positivo"` | Toast de advertencia |
| Email ya registrado | 409 | `"Ya existe una cuenta con ese email"` | Mensaje de error en formulario de registro |
| BD caida / error interno | 500 | `"Error en base de datos"` | Toast: "Hubo un problema con tu pedido, intentalo de nuevo" |

## 3) Medidas de Seguridad

- **SQL Injection**: Todas las consultas usan `PreparedStatement` con parametros tipados (`?`). Ninguna query concatena strings del usuario. Ejemplo: `SELECT precio, stock FROM Productos WHERE id = ?`.
- **No confiar en el cliente (Never Trust the Client)**: El frontend NO envia precios ni total. La API lee el precio real desde la tabla `Productos` y calcula `total = SUM(precio * cantidad)` en el servidor. Si alguien modifica el precio en el inspector del navegador, no tiene efecto.
- **Consistencia transaccional**: El checkout usa transaccion explicita (`setAutoCommit(false)`, `commit`, `rollback`) para que si falla cualquier paso (insertar linea o restar stock) se deshaga todo el pedido.
- **Control de stock en servidor**: Se valida stock antes de insertar lineas y se descuenta con condicion `WHERE stock >= cantidad`. Si la condicion no se cumple, se lanza `rollback` y se devuelve error `409`.
- **Hashing de contrasenas**: Las contrasenas se almacenan con `BCrypt.hashpw()`. Nunca se guardan en texto plano.
- **XSS en frontend**: En las vistas de producto se escapan todos los textos con la funcion `esc()`. El checkout no renderiza datos de usuario con `innerHTML` libre.
