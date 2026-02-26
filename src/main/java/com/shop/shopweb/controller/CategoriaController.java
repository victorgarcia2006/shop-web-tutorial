package com.shop.shopweb.controller;

import com.shop.shopweb.model.Categoria;
import com.shop.shopweb.model.Usuario;
import com.shop.shopweb.service.CategoriaService;
import com.shop.shopweb.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    // GET /api/categorias
    @GetMapping
    public ResponseEntity<Map<String, Object>> listar(HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        List<Categoria> categorias = categoriaService.listarPorUsuario(usuarioId);
        respuesta.put("exito", true);
        respuesta.put("categorias", categorias);
        return ResponseEntity.ok(respuesta);
    }

    // GET /api/categorias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(
            @PathVariable Integer id,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        Optional<Categoria> categoria = categoriaService.buscarPorId(id);
        if (categoria.isPresent()) {
            respuesta.put("exito", true);
            respuesta.put("categoria", categoria.get());
        } else {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "Categoría no encontrada");
            return ResponseEntity.status(404).body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }

    // POST /api/categorias
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(
            @RequestParam String nombre,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Categoria nueva = categoriaService.crear(nombre, usuario);
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Categoría creada correctamente");
            respuesta.put("categoria", nueva);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    // PUT /api/categorias/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestParam String nombre,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        try {
            Categoria actualizada = categoriaService.actualizar(id, nombre, usuarioId);
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Categoría actualizada correctamente");
            respuesta.put("categoria", actualizada);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    // DELETE /api/categorias/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(
            @PathVariable Integer id,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        try {
            categoriaService.eliminar(id, usuarioId);
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Categoría eliminada correctamente");
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }
    }
}