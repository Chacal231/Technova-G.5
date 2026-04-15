package com.Grupo5.technova.controller;

import com.Grupo5.technova.DTO.RegisterRequest;
import com.Grupo5.technova.model.Usuarios;
import com.Grupo5.technova.repository.UsuariosRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UsuariosController {
    
    private final UsuariosRepository repository;

    public UsuariosController(UsuariosRepository repository) {
        this.repository = repository;
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        JsonObject respuesta = new JsonObject();

        // Validar campos obligatorios
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String password = request.getPassword();
        String nombre = request.getNombre() != null ? request.getNombre().trim() : "";

        if (email.isEmpty() || password == null || password.isEmpty() || nombre.isEmpty()) {
            respuesta.addProperty("error", "Todos los campos son obligatorios");
            return ResponseEntity.badRequest().body(respuesta.toString());
        }

        // Validar formato de email basico
        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            respuesta.addProperty("error", "Email no válido");
            return ResponseEntity.badRequest().body(respuesta.toString());
        }

        // Validar longitud de contraseña
        if (password.length() < 6) {
            respuesta.addProperty("error", "La contraseña debe tener al menos 6 caracteres");
            return ResponseEntity.badRequest().body(respuesta.toString());
        }

        // Verificar que el email no este ya registrado
        Usuarios existente = repository.buscarPorEmail(email);
        if (existente != null) {
            respuesta.addProperty("error", "Ya existe una cuenta con ese email");
            return ResponseEntity.status(409).body(respuesta.toString());
        }

        try {
            // Hashear contraseña con BCrypt
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            int nuevoId = repository.registrar(nombre, email, hash);

            if (nuevoId == -1) {
                respuesta.addProperty("error", "Error al crear la cuenta");
                return ResponseEntity.status(500).body(respuesta.toString());
            }

            // Devolver datos del nuevo usuario para auto-login en frontend
            respuesta.addProperty("id", nuevoId);
            respuesta.addProperty("nombre", nombre);
            respuesta.addProperty("email", email);
            respuesta.addProperty("rol", "CLIENTE");
            return ResponseEntity.status(201).body(respuesta.toString());
        } catch (Exception e) {
            respuesta.addProperty("error", "Error interno al crear la cuenta");
            return ResponseEntity.status(500).body(respuesta.toString());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuarios usuario) {
        // Normalizar email (evitar espacios accidentales)
        String emailIntroducido = usuario.getEmail() != null
                ? usuario.getEmail().trim()
                : null;

        System.out.println("[LOGIN] Intento de login para email: '" + emailIntroducido + "'");

        // 1. Buscar usuario por email
        Usuarios usuarioConHash = repository.buscarPorEmail(emailIntroducido);

        // 2. Si el usuario no existe → error genérico
        if (usuarioConHash == null) {
            System.out.println("[LOGIN] Usuario no encontrado en BD");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 3. Verificar contraseña con soporte a contraseñas antiguas en texto plano
        String passwordAlmacenada = usuarioConHash.getPassword();
        if (passwordAlmacenada != null) {
            passwordAlmacenada = passwordAlmacenada.trim(); // por si viene con espacios
            usuarioConHash.setPassword(passwordAlmacenada);
            System.out.println("[LOGIN] Password almacenada (trim) length=" + passwordAlmacenada.length());
        }
        String passwordIntroducida = usuario.getPassword();

        boolean esHashBCrypt = passwordAlmacenada != null && passwordAlmacenada.startsWith("$2");
        boolean passwordCorrecta;

        if (esHashBCrypt) {
            System.out.println("[LOGIN] Detectado hash BCrypt en BD");
            // Caso normal: la BD ya almacena un hash BCrypt
            passwordCorrecta = BCrypt.checkpw(passwordIntroducida, passwordAlmacenada);
        } else {
            System.out.println("[LOGIN] Contraseña en BD tratada como texto plano");
            // Caso legado: contraseña almacenada en texto plano
            passwordCorrecta = passwordIntroducida != null && passwordIntroducida.equals(passwordAlmacenada);

            // Si coincide en texto plano, migramos automáticamente a BCrypt
            if (passwordCorrecta) {
                String nuevoHash = BCrypt.hashpw(passwordIntroducida, BCrypt.gensalt());
                repository.actualizarPasswordHash(usuarioConHash.getId(), nuevoHash);
                usuarioConHash.setPassword(nuevoHash);
            }
        }

        // 4. Si contraseña incorrecta → error
        if (!passwordCorrecta) {
            System.out.println("[LOGIN] Contraseña INCORRECTA para email: '" + emailIntroducido + "'");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(errorJson.toString());
        }

        // 5. Llamar al procedimiento con el hash
        Usuarios usuarioValidado = repository.comprobarLogin(
            usuario.getEmail(), 
            usuarioConHash.getPassword()
        );

        // 6. Si el procedimiento falla (no debería) → error
        if (usuarioValidado == null) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Error en la validación");
            return ResponseEntity.status(500).body(errorJson.toString());
        }

        // 7. Todo bien → devolver datos del usuario
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("id", usuarioConHash.getId());
        responseJson.addProperty("email", usuarioConHash.getEmail());
        String nombre = usuarioConHash.getNombre();
        if (nombre != null && !nombre.trim().isEmpty()) {
            responseJson.addProperty("nombre", nombre.trim());
        }
        responseJson.addProperty("rol", usuarioConHash.getRol());
        
        return ResponseEntity.ok(responseJson.toString());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<String> listarUsuarios(
            @RequestHeader(value = "user-role", required = false) String rol) {
        if (!tieneRol(rol, "ADMIN", "OFICINA")) {
            return respuestaError(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN u OFICINA");
        }
        List<Usuarios> usuarios = repository.listarUsuarios();
        JsonArray array = new JsonArray();
        for (Usuarios u : usuarios) {
            JsonObject json = new JsonObject();
            json.addProperty("id", u.getId());
            json.addProperty("nombre", u.getNombre());
            json.addProperty("email", u.getEmail());
            json.addProperty("rol", u.getRol());
            array.add(json);
        }
        return ResponseEntity.ok(array.toString());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<String> actualizarUsuario(
            @PathVariable int id,
            @RequestBody Usuarios request,
            @RequestHeader(value = "user-role", required = false) String rol) {
        if (!tieneRol(rol, "ADMIN")) {
            return respuestaError(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN");
        }
        if (request == null) {
            return respuestaError(HttpStatus.BAD_REQUEST, "Cuerpo de petición inválido");
        }

        String nombre = request.getNombre() != null ? request.getNombre().trim() : "";
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String rolNuevo = request.getRol() != null ? request.getRol().trim().toUpperCase() : "";

        if (nombre.isEmpty() || email.isEmpty() || rolNuevo.isEmpty()) {
            return respuestaError(HttpStatus.BAD_REQUEST, "Nombre, email y rol son obligatorios");
        }
        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            return respuestaError(HttpStatus.BAD_REQUEST, "Email no válido");
        }
        if (!tieneRol(rolNuevo, "ADMIN", "OFICINA", "CLIENTE")) {
            return respuestaError(HttpStatus.BAD_REQUEST, "Rol inválido");
        }
        if (!repository.existePorId(id)) {
            return respuestaError(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        if (repository.existeOtroUsuarioConEmail(id, email)) {
            return respuestaError(HttpStatus.CONFLICT, "Ya existe otro usuario con ese email");
        }

        boolean actualizado = repository.actualizarUsuario(id, nombre, email, rolNuevo);
        if (!actualizado) {
            return respuestaError(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo actualizar el usuario");
        }
        JsonObject ok = new JsonObject();
        ok.addProperty("mensaje", "Usuario actualizado correctamente");
        ok.addProperty("id", id);
        return ResponseEntity.ok(ok.toString());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable int id,
            @RequestHeader(value = "user-role", required = false) String rol) {
        if (!tieneRol(rol, "ADMIN")) {
            return respuestaError(HttpStatus.FORBIDDEN, "Acceso denegado. Se requiere rol ADMIN");
        }
        if (!repository.existePorId(id)) {
            return respuestaError(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        boolean eliminado = repository.eliminarUsuario(id);
        if (!eliminado) {
            return respuestaError(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo eliminar el usuario");
        }
        JsonObject ok = new JsonObject();
        ok.addProperty("mensaje", "Usuario eliminado correctamente");
        return ResponseEntity.ok(ok.toString());
    }

    private boolean tieneRol(String rolRecibido, String... permitidos) {
        if (rolRecibido == null) return false;
        for (String permitido : permitidos) {
            if (permitido.equalsIgnoreCase(rolRecibido.trim())) {
                return true;
            }
        }
        return false;
    }

    private ResponseEntity<String> respuestaError(HttpStatus status, String mensaje) {
        JsonObject error = new JsonObject();
        error.addProperty("error", mensaje);
        return ResponseEntity.status(status).body(error.toString());
    }
}