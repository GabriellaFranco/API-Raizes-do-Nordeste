package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.configuration.jwt.JwtService;
import com.enterprise.raizesnordeste.domain.dto.request.LoginRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.RegistroRequestDTO;
import com.enterprise.raizesnordeste.domain.entity.Cliente;
import com.enterprise.raizesnordeste.domain.entity.Fidelidade;
import com.enterprise.raizesnordeste.domain.entity.PerfilAutoridade;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.AuditoriaLoginMapper;
import com.enterprise.raizesnordeste.domain.mapper.ClienteMapper;
import com.enterprise.raizesnordeste.domain.mapper.UsuarioMapper;
import com.enterprise.raizesnordeste.repository.*;
import com.enterprise.raizesnordeste.util.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private FidelidadeRepository fidelidadeRepository;
    @Mock private PerfilAutoridadeRepository perfilAutoridadeRepository;
    @Mock private AuditoriaLoginRepository auditoriaLoginRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private ClienteMapper clienteMapper;
    @Mock private AuditoriaLoginMapper auditoriaLoginMapper;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UsuarioValidator usuarioValidator;

    private RegistroRequestDTO registroRequest;
    private LoginRequestDTO loginRequest;
    private Usuario usuario;
    private Cliente cliente;
    private PerfilAutoridade perfilCliente;

    @BeforeEach
    void setUp() {
        registroRequest = new RegistroRequestDTO(
                "João Silva",
                "joao@email.com",
                "52998224725",
                "Senha@123",
                LocalDate.of(1990, 1, 1),
                "47999999999",
                "Rua Teste, 123",
                true
        );

        loginRequest = new LoginRequestDTO("joao@email.com", "Senha@123");

        perfilCliente = PerfilAutoridade.builder()
                .id(1L)
                .nome("CLIENTE")
                .status(true)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("52998224725")
                .senha("senhaCriptografada")
                .status(true)
                .perfis(Set.of(perfilCliente))
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .usuario(usuario)
                .statusConsentimento(true)
                .anonimizado(false)
                .build();
    }

    @Test
    @DisplayName("Registro - deve registrar cliente com sucesso")
    void registro_deveRegistrarComSucesso() {
        when(perfilAutoridadeRepository.findByNome("CLIENTE"))
                .thenReturn(Optional.of(perfilCliente));
        when(usuarioMapper.toEntity(any(RegistroRequestDTO.class)))
                .thenReturn(usuario);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any()))
                .thenReturn(usuario);
        when(clienteMapper.toEntity(any(RegistroRequestDTO.class), any(Usuario.class)))
                .thenReturn(cliente);
        when(clienteRepository.save(any()))
                .thenReturn(cliente);
        when(fidelidadeRepository.save(any()))
                .thenReturn(new Fidelidade());

        var response = authService.registro(registroRequest);

        assertNotNull(response);
        assertEquals(usuario.getId(), response.idUsuario());
        assertEquals(cliente.getId(), response.idCliente());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        verify(usuarioRepository).save(any());
        verify(clienteRepository).save(any());
        verify(fidelidadeRepository).save(any());
    }

    @Test
    @DisplayName("Registro - deve lançar exceção para CPF inválido")
    void registro_deveLancarExcecaoParaCpfInvalido() {
        doThrow(new IllegalArgumentException("CPF inválido"))
                .when(usuarioValidator).validarCpf(anyString());

        assertThrows(IllegalArgumentException.class,
                () -> authService.registro(registroRequest));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registro - deve lançar exceção para email já cadastrado")
    void registro_deveLancarExcecaoParaEmailDuplicado() {
        doNothing().when(usuarioValidator).validarCpf(anyString());
        doThrow(new IllegalArgumentException("Email já cadastrado"))
                .when(usuarioValidator).validarEmailUnico(anyString());

        assertThrows(IllegalArgumentException.class,
                () -> authService.registro(registroRequest));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registro - deve lançar exceção para CPF já cadastrado")
    void registro_deveLancarExcecaoParaCpfDuplicado() {
        doNothing().when(usuarioValidator).validarCpf(anyString());
        doNothing().when(usuarioValidator).validarEmailUnico(anyString());
        doThrow(new IllegalArgumentException("CPF já cadastrado"))
                .when(usuarioValidator).validarCpfUnico(anyString());

        assertThrows(IllegalArgumentException.class,
                () -> authService.registro(registroRequest));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registro - deve lançar exceção quando perfil CLIENTE não encontrado")
    void registro_deveLancarExcecaoQuandoPerfilNaoEncontrado() {
        doNothing().when(usuarioValidator).validarCpf(anyString());
        doNothing().when(usuarioValidator).validarEmailUnico(anyString());
        doNothing().when(usuarioValidator).validarCpfUnico(anyString());
        when(perfilAutoridadeRepository.findByNome("CLIENTE"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> authService.registro(registroRequest));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login - deve autenticar com sucesso")
    void login_deveAutenticarComSucesso() {
        var userDetails = mock(UserDetails.class);

        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(usuario));
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(userDetails);
        when(userDetails.getAuthorities())
                .thenReturn(Collections.emptyList());
        when(jwtService.generateToken(any()))
                .thenReturn("token");
        when(jwtService.generateRefreshToken(any()))
                .thenReturn("refreshToken");
        when(auditoriaLoginMapper.toAuditoriaLogin(any(), anyString(), anyBoolean()))
                .thenReturn(null);

        var response = authService.login(loginRequest, "127.0.0.1");

        assertNotNull(response);
        assertEquals("token", response.token());
        assertEquals("refreshToken", response.refreshToken());
        assertEquals(usuario.getEmail(), response.email());
        assertEquals(usuario.getNome(), response.nome());
    }

    @Test
    @DisplayName("Login - deve lançar exceção para usuário não encontrado")
    void login_deveLancarExcecaoParaUsuarioNaoEncontrado() {
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest, "127.0.0.1"));

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Login - deve lançar exceção para usuário inativo")
    void login_deveLancarExcecaoParaUsuarioInativo() {
        usuario.setStatus(false);
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(usuario));

        assertThrows(DisabledException.class,
                () -> authService.login(loginRequest, "127.0.0.1"));

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Login - deve lançar exceção para credenciais inválidas")
    void login_deveLancarExcecaoParaCredenciaisInvalidas() {
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(usuario));
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest, "127.0.0.1"));
    }

    @Test
    @DisplayName("Login - deve registrar auditoria em caso de falha")
    void login_deveRegistrarAuditoriaEmFalha() {
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(usuario));
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager).authenticate(any());
        when(auditoriaLoginMapper.toAuditoriaLogin(any(), anyString(), anyBoolean()))
                .thenReturn(null);

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest, "127.0.0.1"));

        verify(auditoriaLoginRepository).save(any());
    }

    @Test
    @DisplayName("Alterar senha - deve alterar com sucesso")
    void alterarSenha_deveAlterarComSucesso() {
        var authentication = mock(Authentication.class);
        var context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("joao@email.com");
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(eq("Senha@123"), anyString())).thenReturn(true);
        when(passwordEncoder.matches(eq("NovaSenha@123"), anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("novaSenhaCriptografada");

        assertDoesNotThrow(() -> authService.alterarSenha("Senha@123", "NovaSenha@123"));
        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("Alterar senha - deve lançar exceção para senha atual incorreta")
    void alterarSenha_deveLancarExcecaoParaSenhaAtualIncorreta() {
        var authentication = mock(Authentication.class);
        var context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("joao@email.com");
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> authService.alterarSenha("senhaErrada", "NovaSenha@123"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Alterar senha - deve lançar exceção para nova senha igual à atual")
    void alterarSenha_deveLancarExcecaoParaNovaSenhaIgualAtual() {
        var authentication = mock(Authentication.class);
        var context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("joao@email.com");
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.alterarSenha("Senha@123", "Senha@123"));

        verify(usuarioRepository, never()).save(any());
    }
}