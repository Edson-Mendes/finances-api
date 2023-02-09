package br.com.emendes.financesapi.config.security.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {

  String generateToken(Authentication authentication);

  boolean isTokenValid(String token);

  Long getUserId(String token);

  String recoverToken(HttpServletRequest request);

}
