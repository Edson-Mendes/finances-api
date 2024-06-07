package br.com.emendes.financesapi.config.security.service;

import br.com.emendes.financesapi.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Implementação de {@link TokenService}.
 */
@Service
public class TokenServiceImpl implements TokenService {

  @Value("${financesapi.jwt.expiration}")
  private String expiration;

  @Value("${financesapi.jwt.secret}")
  private String secret;

  @Override
  public String generateToken(Authentication authentication) {
    User logged = (User) authentication.getPrincipal();
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + Long.parseLong(expiration));

    return Jwts.builder()
        .subject(logged.getId().toString())
        .issuedAt(now)
        .expiration(expirationDate)
        .issuer("Finances API")
        .signWith(getSigningKey())
        .compact();
  }

  @Override
  public boolean isTokenValid(String token) {
    try {
      extractAllClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Long getUserId(String token) {
    Claims claims = extractAllClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  @Override
  public String recoverToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      return null;
    }
    return token.substring(7);
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

}
