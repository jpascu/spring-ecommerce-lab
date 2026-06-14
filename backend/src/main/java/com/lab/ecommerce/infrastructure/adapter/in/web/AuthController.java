package com.lab.ecommerce.infrastructure.adapter.in.web;

import com.lab.ecommerce.infrastructure.adapter.in.web.dto.LoginRequest;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.TokenResponse;
import com.lab.ecommerce.infrastructure.security.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de autenticación: valida credenciales y emite un JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login y emisión de tokens JWT")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenService jwtTokenService;

  @Operation(summary = "Login",
      description = "Devuelve un JWT. Usuarios de ejemplo: user/password (USER), admin/password (ADMIN).")
  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    String token = jwtTokenService.generateToken(authentication);
    return TokenResponse.bearer(token, jwtTokenService.expiresInSeconds());
  }
}
