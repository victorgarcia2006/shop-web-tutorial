package com.shop.shopweb.controller;

import com.shop.shopweb.model.Usuario;
import com.shop.shopweb.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();

        Optional<Usuario> usuario = usuarioService.login(username, password);

        if (usuario.isPresent()) {
            // Guardamos el usuario en sesión
            session.setAttribute("usuarioId", usuario.get().getId());
            session.setAttribute("usuarioNombre", usuario.get().getUsername());

            respuesta.put("exito", true);
            respuesta.put("mensaje", "Login exitoso");
            respuesta.put("usuario", usuario.get().getUsername());
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "Usuario o contraseña incorrectos");
            return ResponseEntity.status(401).body(respuesta);
        }
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate(); // destruye toda la sesión
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Sesión cerrada");
        return ResponseEntity.ok(respuesta);
    }

    // GET /api/auth/sesion — el frontend pregunta si hay sesión activa
    @GetMapping("/sesion")
    public ResponseEntity<Map<String, Object>> verificarSesion(HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId != null) {
            respuesta.put("activa", true);
            respuesta.put("usuario", session.getAttribute("usuarioNombre"));
        } else {
            respuesta.put("activa", false);
        }
        return ResponseEntity.ok(respuesta);
    }
}