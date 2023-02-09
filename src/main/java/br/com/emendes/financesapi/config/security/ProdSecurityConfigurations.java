package br.com.emendes.financesapi.config.security;

import br.com.emendes.financesapi.config.security.service.TokenService;
import br.com.emendes.financesapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@Profile("prod")
public class ProdSecurityConfigurations extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;

  private final TokenService tokenService;

  private final UserService userService;

  @Override
  @Bean
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  // Configurações de autenticação
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
  }

  // Configurações de autorização
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    String roleAdmin = "ADMIN";
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/auth/signin").permitAll()
        .antMatchers(HttpMethod.POST, "/auth/signup").permitAll()
        .antMatchers(HttpMethod.GET, "/roles").hasRole(roleAdmin)
        .antMatchers(HttpMethod.GET, "/roles/*").hasRole(roleAdmin)
        .antMatchers(HttpMethod.GET, "/users").hasRole(roleAdmin)
        .antMatchers(HttpMethod.DELETE, "/users/*").hasRole(roleAdmin)
        .anyRequest().authenticated()
        .and().csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().addFilterBefore(new AuthenticationByTokenFilter(tokenService,
                userService),
            UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
  }

  // Configurações de recursos estáticos(js, css, img, etc)
  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers("/swagger-ui/index.html", "/**.html", "/api-docs/**", "/webjars/**", "/configuration/**",
            "/swagger-resources/**", "/swagger-ui/**");
  }

}