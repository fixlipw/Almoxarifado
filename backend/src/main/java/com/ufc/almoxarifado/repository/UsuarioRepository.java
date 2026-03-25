package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
}

