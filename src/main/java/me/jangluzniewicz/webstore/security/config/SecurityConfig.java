package me.jangluzniewicz.webstore.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {
  private final JwtService jwtService;

  public SecurityConfig(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
    JwtFilter jwtFilter = new JwtFilter(authenticationManager, jwtService);
    BearerTokenFilter bearerTokenFilter = new BearerTokenFilter(jwtService);
    http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.csrf(AbstractHttpConfigurer::disable);
    http.addFilterBefore(jwtFilter, AuthorizationFilter.class);
    http.addFilterBefore(bearerTokenFilter, AuthorizationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
