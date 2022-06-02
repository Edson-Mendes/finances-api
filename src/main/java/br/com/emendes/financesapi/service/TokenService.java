package br.com.emendes.financesapi.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

  @Value("${financesapi.jwt.expiration}")
  private String expiration;

  @Value("${financesapi.jwt.secret}")
  private String secret;

  public String generateToken(Authentication authentication) {
    User logged = (User) authentication.getPrincipal();
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + Long.parseLong(expiration));
    
    return Jwts.builder()
            .setIssuer("Finances API")
            .setSubject(logged.getId().toString())
            .setIssuedAt(now)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Long getUserId(String token) {
    Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
    return Long.parseLong(claims.getSubject());
  }

  public Long getUserId(HttpServletRequest request){
    String token = recoverToken(request);
    Long id = getUserId(token);
    return id;
  }

  public String recoverToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if(token == null || token.isEmpty() || !token.startsWith("Bearer ")){
      return null;
    }
    return token.substring(7);
  }

}