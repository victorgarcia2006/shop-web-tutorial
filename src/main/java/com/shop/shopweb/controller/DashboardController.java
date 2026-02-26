package com.shop.shopweb.controller;

import com.shop.shopweb.service.CategoriaService;
import com.shop.shopweb.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    // GET /api/dashboard/estadisticas
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> estadisticas(HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();

        // Verificar sesión activa
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "No autorizado");
            return ResponseEntity.status(401).body(respuesta);
        }

        long totalProductos   = productoService.contarPorUsuario(usuarioId);
        long totalCategorias  = categoriaService.contarPorUsuario(usuarioId);

        respuesta.put("exito", true);
        respuesta.put("usuario", session.getAttribute("usuarioNombre"));
        respuesta.put("totalProductos", totalProductos);
        respuesta.put("totalCategorias", totalCategorias);
        return ResponseEntity.ok(respuesta);
    }
}