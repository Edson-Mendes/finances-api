package br.com.emendes.financesapi.config.security.filter;

import br.com.emendes.financesapi.config.security.service.TokenService;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro responsável por autenticar requisição via JWT.
 */
@RequiredArgsConstructor
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UserService userService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String token = tokenService.recoverToken(request);
    boolean isValid = tokenService.isTokenValid(token);
    if (isValid) {
      authenticateClient(token);
    }
    filterChain.doFilter(request, response);
  }

  private void authenticateClient(String token) {
    Long userId = tokenService.getUserId(token);
    User user = userService.readById(userId);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
        user.getRoles());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}
