package com.shop.shopweb.repository;

import com.shop.shopweb.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Obtiene solo las categorías del usuario logueado
    List<Categoria> findByUsuarioId(Integer usuarioId);

    // Cuenta cuántas categorías tiene un usuario (para el dashboard)
    long countByUsuarioId(Integer usuarioId);
}