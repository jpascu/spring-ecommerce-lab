package com.lab.ecommerce.infrastructure.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security con autenticación stateless por <strong>JWT</strong>.
 *
 * <p>La app actúa como <em>resource server</em>: valida el token en cada petición
 * (no hay sesión). El login emite un JWT firmado con HMAC (HS256) y un secreto
 * compartido. Las autoridades se leen del claim {@code roles}.</p>
 */
@Configuration
public class SecurityConfig {

  private final SecretKey secretKey;

  public SecurityConfig(@Value("${security.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // API stateless con JWT: CSRF no aplica (no usamos cookies de sesión)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // públicos: login, documentación, observabilidad y consola H2
            .requestMatchers("/api/auth/**", "/swagger-ui/**", "/swagger-ui.html",
                "/v3/api-docs/**", "/actuator/**", "/h2-console/**").permitAll()
            // el presupuesto lo puede pedir cualquier usuario autenticado
            .requestMatchers(HttpMethod.POST, "/api/products/*/quote").authenticated()
            // escritura del catálogo: solo ADMIN
            .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
            // el resto requiere autenticación (lectura de catálogo, tipos de cambio...)
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
            .jwtAuthenticationConverter(jwtAuthenticationConverter())))
        // permite que la consola H2 se muestre en un frame
        .headers(headers -> headers.frameOptions(frame -> frame.disable()));
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /** Usuarios de ejemplo en memoria (en producción vendrían de BBDD/IdP). */
  @Bean
  public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
    UserDetails user = User.withUsername("user")
        .password(encoder.encode("password")).roles("USER").build();
    UserDetails admin = User.withUsername("admin")
        .password(encoder.encode("password")).roles("USER", "ADMIN").build();
    return new InMemoryUserDetailsManager(user, admin);
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(secretKey).build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /** Convierte el claim "roles" (p.ej. ["ROLE_ADMIN"]) en authorities de Spring. */
  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
    authorities.setAuthorityPrefix("");           // los roles ya vienen con prefijo ROLE_
    authorities.setAuthoritiesClaimName("roles");
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(authorities);
    return converter;
  }
}
