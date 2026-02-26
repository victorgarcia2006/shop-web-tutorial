package com.shop.shopweb.service;

import com.shop.shopweb.model.Usuario;
import com.shop.shopweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Login: convierte el password a MD5 antes de comparar con la BD
    public Optional<Usuario> login(String username, String password) {
        String passwordMD5 = DigestUtils.md5DigestAsHex(password.getBytes());
        return usuarioRepository.findByUsernameAndPassword(username, passwordMD5);
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
}
