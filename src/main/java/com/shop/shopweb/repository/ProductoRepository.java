package com.shop.shopweb.repository;

import com.shop.shopweb.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Obtiene solo los productos del usuario logueado
    List<Producto> findByUsuarioId(Integer usuarioId);

    // Cuenta cuántos productos tiene un usuario (para el dashboard)
    long countByUsuarioId(Integer usuarioId);

    // Busca productos por categoría y usuario (útil para filtros futuros)
    List<Producto> findByCategoriaIdAndUsuarioId(Integer categoriaId, Integer usuarioId);
}
