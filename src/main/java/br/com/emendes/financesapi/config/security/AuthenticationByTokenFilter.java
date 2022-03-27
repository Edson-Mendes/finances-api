package br.com.emendes.financesapi.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;

public class AuthenticationByTokenFilter extends OncePerRequestFilter {

  private TokenService tokenService;
  private UserRepository userRepository;

  public AuthenticationByTokenFilter(TokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = tokenService.recoverToken(request);
    boolean isValid = tokenService.isTokenValid(token);
    if (isValid) {
      authenticateClient(token);
    }
    filterChain.doFilter(request, response);
  }

  private void authenticateClient(String token) {
    Long userId = tokenService.getUserId(token);
    User user = userRepository.findById(userId).orElse(null);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
        user.getRoles());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}
