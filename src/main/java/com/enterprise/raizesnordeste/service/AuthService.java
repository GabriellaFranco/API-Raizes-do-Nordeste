package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.configuration.jwt.JwtService;
import com.enterprise.raizesnordeste.domain.dto.request.LoginRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.RegistroRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.AuthResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.RegistroResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.AuditoriaLogin;
import com.enterprise.raizesnordeste.domain.entity.Fidelidade;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.AuditoriaLoginMapper;
import com.enterprise.raizesnordeste.domain.mapper.ClienteMapper;
import com.enterprise.raizesnordeste.domain.mapper.UsuarioMapper;
import com.enterprise.raizesnordeste.repository.*;
import com.enterprise.raizesnordeste.util.UsuarioValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String PERFIL_CLIENTE = "CLIENTE";

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PerfilAutoridadeRepository perfilAutoridadeRepository;
    private final AuditoriaLoginRepository auditoriaLoginRepository;
    private final UsuarioMapper usuarioMapper;
    private final ClienteMapper clienteMapper;
    private final AuditoriaLoginMapper auditoriaLoginMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioValidator usuarioValidator;

    @Transactional
    public RegistroResponseDTO registro(RegistroRequestDTO request) {
        usuarioValidator.validarCpf(request.cpf());
        usuarioValidator.validarEmailUnico(request.email());
        usuarioValidator.validarCpfUnico(request.cpf());

        var perfil = perfilAutoridadeRepository.findByNome(PERFIL_CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Perfil CLIENTE não encontrado"));

        var usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setPerfis(Set.of(perfil));
        usuarioRepository.save(usuario);

        var cliente = clienteMapper.toEntity(request, usuario);
        cliente.setDataConsentimento(LocalDateTime.now());
        clienteRepository.save(cliente);

        Fidelidade fidelidade = Fidelidade.builder()
                .cliente(cliente)
                .build();
        fidelidadeRepository.save(fidelidade);

        return RegistroResponseDTO.builder()
                .idUsuario(usuario.getId())
                .idCliente(cliente.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();

    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request, String ip) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(request.email())
                    .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos"));

            if (!usuario.getStatus()) {
                throw new DisabledException("Usuário inativo");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );

            var userDetails = userDetailsService.loadUserByUsername(request.email());

            registrarAuditoria(usuario, ip, true);

            var token = jwtService.generateToken(userDetails);
            var refreshToken = jwtService.generateRefreshToken(userDetails);

            Set<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toSet());

            return new AuthResponseDTO(token, refreshToken, usuario.getEmail(), usuario.getNome(), roles);

        } catch (BadCredentialsException e) {
            usuarioRepository.findByEmail(request.email())
                    .ifPresent(usuario -> registrarAuditoria(usuario, ip, false));
            throw new BadCredentialsException("Email ou senha inválidos");
        }
    }

    public AuthResponseDTO refresh(String refreshToken) {
        var email = jwtService.extractUsername(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!usuario.getStatus()) {
            throw new DisabledException("Usuário inativo");
        }

        String newToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toSet());

        return new AuthResponseDTO(newToken, newRefreshToken, usuario.getEmail(), usuario.getNome(), roles);
    }

    @Transactional
    public void alterarSenha(String senhaAtual, String novaSenha) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        if (passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            throw new IllegalArgumentException("Nova senha não pode ser igual à senha atual");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    private void registrarAuditoria(Usuario usuario, String ip, Boolean sucesso) {
        AuditoriaLogin auditoria = auditoriaLoginMapper.toAuditoriaLogin(usuario, ip, sucesso);
        auditoriaLoginRepository.save(auditoria);
    }
}

