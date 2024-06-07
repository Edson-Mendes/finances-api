package br.com.emendes.financesapi.config.security;

import br.com.emendes.financesapi.config.security.filter.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static br.com.emendes.financesapi.util.constant.RoleConstant.ROLE_ADMIN;

/**
 * Classe com as configurações de segurança.
 */
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

  private final JWTAuthenticationFilter authenticationFilter;
  private static final String[] SWAGGER_WHITELIST = {"/swagger-ui/index.html", "/**.html", "/api-docs/**",
      "/webjars/**", "/configuration/**", "/swagger-resources/**", "/swagger-ui/**"};

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(CsrfConfigurer::disable);

    http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exceptionHandling -> {
          exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
          exceptionHandling.accessDeniedHandler(
              ((request, response, accessDeniedException) -> response.setStatus(HttpStatus.FORBIDDEN.value())));
        });

    http.authorizeHttpRequests(authorize -> {
      authorize.requestMatchers(HttpMethod.GET, SWAGGER_WHITELIST).permitAll();
      authorize.requestMatchers(HttpMethod.POST, "/api/auth/*").permitAll();
      authorize.requestMatchers(HttpMethod.GET, "/api/users").hasRole(ROLE_ADMIN);
      authorize.requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole(ROLE_ADMIN);
      authorize.anyRequest().authenticated();
    });

    return http.build();
  }

}