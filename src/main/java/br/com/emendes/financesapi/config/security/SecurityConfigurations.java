package br.com.emendes.financesapi.config.security;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.emendes.financesapi.repository.UserRepository;

@EnableWebSecurity
@Configuration
@Profile("dev")
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private UserRepository userRepository;

  @Override
  @Bean
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  // Configurações de autenticação
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(authenticationService).passwordEncoder(new BCryptPasswordEncoder());
  }

  // Configurações de autorização
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/auth/signin").permitAll()
        .antMatchers(HttpMethod.POST, "/auth/signup").permitAll()
        .antMatchers(HttpMethod.POST, "/role").hasRole("ADMIN")
        .antMatchers(HttpMethod.GET, "/role").hasRole("ADMIN")
        .antMatchers(HttpMethod.GET, "/role/*").hasRole("ADMIN")
        .antMatchers(HttpMethod.DELETE, "/role/*").hasRole("ADMIN")
        .antMatchers(HttpMethod.DELETE, "/user/*").hasRole("ADMIN")
        .anyRequest().authenticated()
        .and().csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().addFilterBefore(new AuthenticationByTokenFilter(tokenService,
            userRepository),
            UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

    // http.authorizeRequests().anyRequest().permitAll().and().csrf().disable().headers().frameOptions().disable();
    // http.authorizeRequests().antMatchers("/").permitAll().and()
    //     .authorizeRequests().antMatchers("/h2-console/**").permitAll();
    // http.csrf().disable();
    // http.headers().frameOptions().disable();
  }

  // Configurações de recursos estáticos(js, css, img, etc)
  @Override
  public void configure(WebSecurity web) throws Exception {

  }

}