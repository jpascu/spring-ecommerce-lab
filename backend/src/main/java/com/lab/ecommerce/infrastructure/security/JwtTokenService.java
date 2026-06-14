package com.lab.ecommerce.infrastructure.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Emite tokens JWT firmados (HS256) a partir de una autenticación válida.
 */
@Service
public class JwtTokenService {

  private static final long EXPIRATION_SECONDS = 3600; // 1 hora

  private final JwtEncoder jwtEncoder;

  public JwtTokenService(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public long expiresInSeconds() {
    return EXPIRATION_SECONDS;
  }

  public String generateToken(Authentication authentication) {
    Instant now = Instant.now();
    List<String> roles = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("spring-ecommerce-lab")
        .issuedAt(now)
        .expiresAt(now.plus(EXPIRATION_SECONDS, ChronoUnit.SECONDS))
        .subject(authentication.getName())
        .claim("roles", roles)
        .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }
}
