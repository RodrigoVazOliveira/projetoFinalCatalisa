package br.com.zup.zupayments.jwt;

import br.com.zup.zupayments.dtos.login.LoginDTO;
import br.com.zup.zupayments.models.Usuario;
import br.com.zup.zupayments.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

public class FiltroAutencicacaoJWT extends UsernamePasswordAuthenticationFilter {
    private final ComponenteJWT componenteJWT;
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;

    public FiltroAutencicacaoJWT(ComponenteJWT componenteJWT, AuthenticationManager authenticationManager, UsuarioService usuarioService) {
        this.componenteJWT = componenteJWT;
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            LoginDTO login = objectMapper.readValue(request.getInputStream(), LoginDTO.class);
            Usuario usuarioLogin =
                    usuarioService.procurarUsuarioPeloEmail(
                            login.email()
                    );
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken
                    (login.email(),
                            login.senha(), List.of(
                            new SimpleGrantedAuthority(String.valueOf(usuarioLogin.getNivelDeAcesso()))
                    ));

            return authenticationManager.authenticate(authToken);
        } catch (IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = ((UsuarioLogin) authResult.getPrincipal()).getUsername();
        String token = componenteJWT.gerarToken(username);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.addHeader("Authorization", "Token " + token);
    }
}