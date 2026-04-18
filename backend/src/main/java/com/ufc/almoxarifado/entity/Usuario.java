package com.ufc.almoxarifado.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String usuario;

    @Column(unique = true)
    private String matricula;

    @Column(nullable = false)
    private String senha;

    private String nome;

    private String sobrenome;

    private String curso;

    private String fotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleAcesso acesso;

    private Boolean isAtivada = false;

    private Boolean isBloqueado = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.acesso == RoleAcesso.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (this.acesso == RoleAcesso.BOLSISTA) {
            return List.of(new SimpleGrantedAuthority("ROLE_BOLSISTA"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_ALUNO"));
        }
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return usuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAtivada;
    }

}