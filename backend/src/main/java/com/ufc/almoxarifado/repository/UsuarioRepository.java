package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByUsuario(String usuario);
    Optional<Usuario> findByMatricula(String matricula);

}
