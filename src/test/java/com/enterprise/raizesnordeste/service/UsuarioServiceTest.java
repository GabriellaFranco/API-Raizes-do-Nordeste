package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.UsuarioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.UsuarioUpdateRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.UsuarioResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.PerfilAutoridade;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.UsuarioMapper;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.PerfilAutoridadeRepository;
import com.enterprise.raizesnordeste.repository.UnidadeRepository;
import com.enterprise.raizesnordeste.repository.UsuarioRepository;
import com.enterprise.raizesnordeste.util.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UnidadeRepository unidadeRepository;
    @Mock private PerfilAutoridadeRepository perfilAutoridadeRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private UsuarioValidator usuarioValidator;
    @Mock private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private Unidade unidade;
    private PerfilAutoridade perfil;
    private UsuarioResponseDTO usuarioResponse;
    private UsuarioRequestDTO usuarioRequest;
    private UsuarioUpdateRequestDTO usuarioUpdateRequest;

    @BeforeEach
    void setUp() {
        unidade = Unidade.builder()
                .id(1L)
                .nomeFantasia("Raízes Recife")
                .cnpj("12345678000195")
                .status(true)
                .build();

        perfil = PerfilAutoridade.builder()
                .id(1L)
                .nome("ATENDENTE")
                .status(true)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("Maria Silva")
                .email("maria@email.com")
                .cpf("52998224725")
                .senha("senhaCriptografada")
                .status(true)
                .unidade(unidade)
                .perfis(new HashSet<>(Set.of(perfil)))
                .build();

        usuarioResponse = new UsuarioResponseDTO(
                1L, "Maria Silva", "maria@email.com",
                "52998224725", true, null, null, null, null,
                Set.of("ATENDENTE")
        );

        usuarioRequest = new UsuarioRequestDTO(
                "Maria Silva", "maria@email.com",
                "52998224725", "Senha@123", 1L, 1L
        );

        usuarioUpdateRequest = new UsuarioUpdateRequestDTO(
                "Maria Souza", "maria.souza@email.com", 1L
        );
    }

    @Test
    @DisplayName("Listar - deve retornar página de usuários")
    void listar_deveRetornarPaginaDeUsuarios() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(usuario));

        when(usuarioRepository.findAll(pageable)).thenReturn(page);
        when(usuarioMapper.toUsuarioResponseDTO(any())).thenReturn(usuarioResponse);

        var result = usuarioService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(usuarioRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Buscar por ID - deve retornar usuário")
    void buscarPorId_deveRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toUsuarioResponseDTO(any())).thenReturn(usuarioResponse);

        var result = usuarioService.getUsuarioById(1L);

        assertNotNull(result);
        assertEquals("Maria Silva", result.nome());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar por ID - deve lançar exceção para usuário não encontrado")
    void buscarPorId_deveLancarExcecaoParaUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.getUsuarioById(99L));
    }

    @Test
    @DisplayName("Criar - deve criar usuário com sucesso")
    void criar_deveCriarUsuarioComSucesso() {
        when(unidadeRepository.findById(anyLong())).thenReturn(Optional.of(unidade));
        when(perfilAutoridadeRepository.findById(anyLong())).thenReturn(Optional.of(perfil));
        when(usuarioMapper.toUsuario(any(), any(), any())).thenReturn(usuario);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(usuarioMapper.toUsuarioResponseDTO(any())).thenReturn(usuarioResponse);

        var result = usuarioService.createUsuario(usuarioRequest);

        assertNotNull(result);
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("Criar - deve lançar exceção para unidade não encontrada")
    void criar_deveLancarExcecaoParaUnidadeNaoEncontrada() {
        doNothing().when(usuarioValidator).validarCpf(anyString());
        doNothing().when(usuarioValidator).validarEmailUnico(anyString());
        doNothing().when(usuarioValidator).validarCpfUnico(anyString());
        when(unidadeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.createUsuario(usuarioRequest));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar - deve lançar exceção para perfil não encontrado")
    void criar_deveLancarExcecaoParaPerfilNaoEncontrado() {
        doNothing().when(usuarioValidator).validarCpf(anyString());
        doNothing().when(usuarioValidator).validarEmailUnico(anyString());
        doNothing().when(usuarioValidator).validarCpfUnico(anyString());
        when(unidadeRepository.findById(anyLong())).thenReturn(Optional.of(unidade));
        when(perfilAutoridadeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.createUsuario(usuarioRequest));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Atualizar - deve atualizar usuário com sucesso")
    void atualizar_deveAtualizarComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(unidadeRepository.findById(anyLong())).thenReturn(Optional.of(unidade));
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(usuarioMapper.toUsuarioResponseDTO(any())).thenReturn(usuarioResponse);

        var result = usuarioService.updateUsuario(1L, usuarioUpdateRequest);

        assertNotNull(result);
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("Atualizar - deve lançar exceção para email duplicado")
    void atualizar_deveLancarExcecaoParaEmailDuplicado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.updateUsuario(1L, usuarioUpdateRequest));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Vincular perfil - deve vincular com sucesso")
    void vincularPerfil_deveVincularComSucesso() {
        var novoPerfil = PerfilAutoridade.builder()
                .id(2L).nome("GERENTE").status(true).build();

        var perfisSet = spy(new HashSet<>(Set.of(perfil)));
        usuario.setPerfis(perfisSet);
        when(perfisSet.contains(novoPerfil)).thenReturn(false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilAutoridadeRepository.findById(2L)).thenReturn(Optional.of(novoPerfil));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        assertDoesNotThrow(() -> usuarioService.vincularPerfilAutoridade(1L, 2L));
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("Vincular perfil - deve lançar exceção para perfil já vinculado")
    void vincularPerfil_deveLancarExcecaoParaPerfilJaVinculado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilAutoridadeRepository.findById(1L)).thenReturn(Optional.of(perfil));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.vincularPerfilAutoridade(1L, 1L));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Desvincular perfil - deve lançar exceção quando usuário tem apenas um perfil")
    void desvincularPerfil_deveLancarExcecaoComApenaUmPerfil() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilAutoridadeRepository.findById(1L)).thenReturn(Optional.of(perfil));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.desvincularPerfil(1L, 1L));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Desvincular perfil - deve lançar exceção para perfil não vinculado")
    void desvincularPerfil_deveLancarExcecaoParaPerfilNaoVinculado() {
        var outroPerfil = PerfilAutoridade.builder()
                .id(2L).nome("GERENTE").status(true).build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilAutoridadeRepository.findById(2L)).thenReturn(Optional.of(outroPerfil));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.desvincularPerfil(1L, 2L));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Alterar status - deve alterar de ativo para inativo")
    void alterarStatus_deveAlternarParaInativo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        usuarioService.alterarStatus(1L);

        assertFalse(usuario.getStatus());
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("Alterar status - deve alterar de inativo para ativo")
    void alterarStatus_deveAlternarParaAtivo() {
        usuario.setStatus(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        usuarioService.alterarStatus(1L);

        assertTrue(usuario.getStatus());
        verify(usuarioRepository).save(any());
    }
}