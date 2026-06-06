package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.UsuarioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.UsuarioUpdateRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.UsuarioResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.UsuarioMapper;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.PerfilAutoridadeRepository;
import com.enterprise.raizesnordeste.repository.UnidadeRepository;
import com.enterprise.raizesnordeste.repository.UsuarioRepository;
import com.enterprise.raizesnordeste.util.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final PerfilAutoridadeRepository perfilAutoridadeRepository;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioValidator usuarioValidator;
    private final PasswordEncoder passwordEncoder;

    public Page<UsuarioResponseDTO> getAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(usuarioMapper::toUsuarioResponseDTO);
    }

    public UsuarioResponseDTO getUsuarioById(Long id) {
        return usuarioMapper.toUsuarioResponseDTO(buscarUsuario(id));
    }

    @Transactional
    public UsuarioResponseDTO createUsuario(UsuarioRequestDTO request) {
        usuarioValidator.validarCpf(request.cpf());
        usuarioValidator.validarEmailUnico(request.email());
        usuarioValidator.validarCpfUnico(request.cpf());

        var unidade = unidadeRepository.findById(request.idUnidade())
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada"));

        var perfil = perfilAutoridadeRepository.findById(request.idPerfil())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado"));

        var usuario = usuarioMapper.toUsuario(request, unidade, perfil);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuarioRepository.save(usuario);

        return usuarioMapper.toUsuarioResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO updateUsuario(Long id, UsuarioUpdateRequestDTO request) {
        Usuario usuario = buscarUsuario(id);

        if (!usuario.getEmail().equals(request.email()) &&
                usuarioRepository.existsByEmail(request.email())) {
            throw new ResourceNotFoundException("Email já cadastrado");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());

        if (request.idUnidade() != null) {
            var unidade = unidadeRepository.findById(request.idUnidade())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada"));
            usuario.setUnidade(unidade);
        }

        usuarioRepository.save(usuario);
        return usuarioMapper.toUsuarioResponseDTO(usuario);
    }

    @Transactional
    public void vincularPerfilAutoridade(Long id, Long idPerfil) {
        Usuario usuario = buscarUsuario(id);
        var perfil = perfilAutoridadeRepository.findById(idPerfil)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado"));

        if (usuario.getPerfis().contains(perfil)) {
            throw new IllegalArgumentException("Usuário já possui esse perfil");
        }

        usuario.getPerfis().add(perfil);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desvincularPerfil(Long id, Long idPerfil) {
        Usuario usuario = buscarUsuario(id);

        var perfil = perfilAutoridadeRepository.findById(idPerfil)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado"));

        if (!usuario.getPerfis().contains(perfil)) {
            throw new ResourceNotFoundException("Usuário não possui esse perfil");
        }

        if (usuario.getPerfis().size() == 1) {
            throw new IllegalArgumentException("Usuário deve ter pelo menos um perfil");
        }

        usuario.getPerfis().remove(perfil);
        usuarioRepository.save(usuario);
    }

    public Page<UsuarioResponseDTO> getAllByUnidade(Long idUnidade, Pageable pageable) {
        unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada: " + idUnidade));

        return usuarioRepository.findAllByUnidadeId(idUnidade, pageable).map(usuarioMapper::toUsuarioResponseDTO);
    }

    @Transactional
    public void alterarStatus(Long id) {
        var usuario = buscarUsuario(id);
        usuario.setStatus(!usuario.getStatus());
        usuarioRepository.save(usuario);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}


