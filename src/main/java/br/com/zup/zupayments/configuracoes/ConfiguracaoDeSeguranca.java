package br.com.zup.zupayments.configuracoes;

import br.com.zup.zupayments.enums.RolesEnum;
import br.com.zup.zupayments.jwt.ComponenteJWT;
import br.com.zup.zupayments.jwt.FiltroAutencicacaoJWT;
import br.com.zup.zupayments.jwt.FiltroDeAutorizacao;
import br.com.zup.zupayments.jwt.UsuarioLoginService;
import br.com.zup.zupayments.repositories.UsuarioRepository;
import br.com.zup.zupayments.services.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class ConfiguracaoDeSeguranca {

    private final ComponenteJWT componenteJWT;
    private final UsuarioLoginService usuarioLoginService;
    private final UsuarioRepository usuarioRepository;

    public ConfiguracaoDeSeguranca(ComponenteJWT componenteJWT, UsuarioLoginService usuarioLoginService, UsuarioRepository usuarioRepository) {
        this.componenteJWT = componenteJWT;
        this.usuarioLoginService = usuarioLoginService;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
        final AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(usuarioLoginService).passwordEncoder(critografarSenha());
        final AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Configuração otimizada para HTTP/2 com Spring Security
        http
                // Desabilitar CSRF (API stateless com JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS configurado
                .cors(cors -> cors.configurationSource(configuracaoDeCors()))

                // Autorização por endpoint
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(HttpMethod.POST, "/fornecedores/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS))
                        .requestMatchers(HttpMethod.GET, "/fornecedores/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS))
                        .requestMatchers(HttpMethod.PATCH, "/fornecedores/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS))
                        .requestMatchers(HttpMethod.PUT, "/fornecedores/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS))
                        .requestMatchers(HttpMethod.GET, "/fornecedores/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS))
                        .requestMatchers(HttpMethod.POST, "/pedidos/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS))
                        .requestMatchers(HttpMethod.GET, "/pedidos/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS), String.valueOf(RolesEnum.PERFIL_FINANCEIRO))
                        .requestMatchers(HttpMethod.PATCH, "/pedidos/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS))
                        .requestMatchers(HttpMethod.POST, "/notas_ficais/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_FINANCEIRO))
                        .requestMatchers(HttpMethod.GET, "/notas_ficais/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_COMPRAS), String.valueOf(RolesEnum.PERFIL_FINANCEIRO))
                        .requestMatchers(HttpMethod.PATCH, "/notas_ficais/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_FINANCEIRO))
                        .requestMatchers(HttpMethod.POST, "/responsaveis/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_FINANCEIRO))
                        .requestMatchers(HttpMethod.GET, "/responsaveis/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_FINANCEIRO))
                        .requestMatchers(HttpMethod.PATCH, "/responsaveis/**").hasAnyAuthority(String.valueOf(RolesEnum.PERFIL_MASTER), String.valueOf(RolesEnum.PERFIL_FINANCEIRO))
                        .requestMatchers(HttpMethod.POST, "/usuarios/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/usuarios/").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/usuarios/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())

                // Session management - Stateless para HTTP/2
                .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authentication manager
                .authenticationManager(authenticationManager)

                // Filtros JWT
                .addFilterBefore(
                    new FiltroAutencicacaoJWT(componenteJWT, authenticationManager, new UsuarioService(usuarioRepository, critografarSenha())),
                    UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                    new FiltroDeAutorizacao(authenticationManager, componenteJWT, usuarioLoginService),
                    UsernamePasswordAuthenticationFilter.class)

                // Headers de segurança (compatível com HTTP/2)
                .headers(headers -> headers
                    .cacheControl(cache -> cache.disable())
                    .httpStrictTransportSecurity(hsts -> hsts.disable()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource configuracaoDeCors() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

        return source;
    }

    @Bean
    BCryptPasswordEncoder critografarSenha() {
        return new BCryptPasswordEncoder();
    }
}
