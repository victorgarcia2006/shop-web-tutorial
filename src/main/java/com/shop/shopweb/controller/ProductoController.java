package com.shop.shopweb.controller;

import com.shop.shopweb.model.Categoria;
import com.shop.shopweb.model.Producto;
import com.shop.shopweb.model.Usuario;
import com.shop.shopweb.service.CategoriaService;
import com.shop.shopweb.service.ProductoService;
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
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    // GET /api/productos — lista productos del usuario logueado
    @GetMapping
    public ResponseEntity<Map<String, Object>> listar(HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        List<Producto> productos = productoService.listarPorUsuario(usuarioId);
        respuesta.put("exito", true);
        respuesta.put("productos", productos);
        return ResponseEntity.ok(respuesta);
    }

    // GET /api/productos/{id} — obtiene un producto por id (para el modal de edición)
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

        Optional<Producto> producto = productoService.buscarPorId(id);
        if (producto.isPresent()) {
            respuesta.put("exito", true);
            respuesta.put("producto", producto.get());
        } else {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "Producto no encontrado");
            return ResponseEntity.status(404).body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }

    // POST /api/productos — crear nuevo producto
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(
            @RequestParam String nombre,
            @RequestParam Double precio,
            @RequestParam Integer stock,
            @RequestParam Double descuento,
            @RequestParam(required = false) String observaciones,
            @RequestParam Integer categoriaId,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        try {
            Categoria categoria = categoriaService.buscarPorId(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Producto nuevo = productoService.crear(
                    nombre, precio, stock, descuento, observaciones, categoria, usuario
            );

            respuesta.put("exito", true);
            respuesta.put("mensaje", "Producto creado correctamente");
            respuesta.put("producto", nuevo);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    // PUT /api/productos/{id} — actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam Double precio,
            @RequestParam Integer stock,
            @RequestParam Double descuento,
            @RequestParam(required = false) String observaciones,
            @RequestParam Integer categoriaId,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        try {
            Categoria categoria = categoriaService.buscarPorId(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            Producto actualizado = productoService.actualizar(
                    id, nombre, precio, stock, descuento, observaciones, categoria, usuarioId
            );

            respuesta.put("exito", true);
            respuesta.put("mensaje", "Producto actualizado correctamente");
            respuesta.put("producto", actualizado);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    // DELETE /api/productos/{id} — eliminar producto
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
            productoService.eliminar(id, usuarioId);
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Producto eliminado correctamente");
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }
    }
}