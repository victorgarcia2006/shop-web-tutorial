package com.shop.shopweb.repository;

import com.shop.shopweb.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Busca usuario por username y password (MD5 se aplica antes de llamar esto)
    Optional<Usuario> findByUsernameAndPassword(String username, String password);

    // Busca solo por username (útil para verificar si existe)
    Optional<Usuario> findByUsername(String username);
}