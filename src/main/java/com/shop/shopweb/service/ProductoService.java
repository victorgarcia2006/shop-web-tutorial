package com.shop.shopweb.service;

import com.shop.shopweb.model.Categoria;
import com.shop.shopweb.model.Producto;
import com.shop.shopweb.model.Usuario;
import com.shop.shopweb.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener todos los productos del usuario logueado
    public List<Producto> listarPorUsuario(Integer usuarioId) {
        return productoRepository.findByUsuarioId(usuarioId);
    }

    // Contar productos del usuario (para el dashboard)
    public long contarPorUsuario(Integer usuarioId) {
        return productoRepository.countByUsuarioId(usuarioId);
    }

    // Buscar un producto por id
    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    // Crear nuevo producto
    public Producto crear(String nombre, Double precio, Integer stock, Double descuento,
                          String observaciones, Categoria categoria, Usuario usuario) {
        validarCampos(nombre, precio, stock);

        Producto producto = new Producto(
                nombre.trim(), precio, stock , descuento, observaciones, categoria, usuario
        );
        return productoRepository.save(producto);
    }

    // Actualizar producto existente
    public Producto actualizar(Integer id, String nombre, Double precio,
                               Integer stock, Double descuento, String observaciones,
                               Categoria categoria, Integer usuarioId) {
        validarCampos(nombre, precio, stock);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que el producto pertenece al usuario logueado
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        producto.setNombre(nombre.trim());
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDescuento(descuento);
        producto.setObservaciones(observaciones);
        producto.setCategoria(categoria);

        return productoRepository.save(producto);
    }

    // Eliminar producto
    public void eliminar(Integer id, Integer usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que el producto pertenece al usuario logueado
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado");
        }

        productoRepository.deleteById(id);
    }

    // Validación reutilizable
    private void validarCampos(String nombre, Double precio, Integer stock) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (precio == null || precio < 0) {
            throw new IllegalArgumentException("El precio no es válido");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no es válido");
        }
    }
}