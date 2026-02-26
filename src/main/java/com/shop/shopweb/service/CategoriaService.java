package com.shop.shopweb.service;

import com.shop.shopweb.model.Categoria;
import com.shop.shopweb.model.Usuario;
import com.shop.shopweb.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Obtener todas las categorías del usuario logueado
    public List<Categoria> listarPorUsuario(Integer usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    // Contar categorías del usuario (para el dashboard)
    public long contarPorUsuario(Integer usuarioId) {
        return categoriaRepository.countByUsuarioId(usuarioId);
    }

    // Buscar una categoría por id
    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    // Crear nueva categoría
    public Categoria crear(String nombre, Usuario usuario) {
        // Validación: nombre no puede estar vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        Categoria categoria = new Categoria(nombre.trim(), usuario);
        return categoriaRepository.save(categoria);
    }

    // Actualizar categoría existente
    public Categoria actualizar(Integer id, String nuevoNombre, Integer usuarioId) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Verificar que la categoría pertenece al usuario logueado
        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        categoria.setNombre(nuevoNombre.trim());
        return categoriaRepository.save(categoria);
    }

    // Eliminar categoría
    public void eliminar(Integer id, Integer usuarioId) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Verificar que la categoría pertenece al usuario logueado
        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        categoriaRepository.deleteById(id);
    }
}