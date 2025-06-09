package br.com.juliocesarcoutinho.userservice.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.juliocesarcoutinho.userservice.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    
    private static final String[] PUBLIC_PATHS = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/h2-console/**",
        "/actuator/**",
        "/auth/**"
};
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // <-- Adicione esta linha!Desabilita o frame options para permitir o H2 Console
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                  authorize.requestMatchers("/h2-console/**").permitAll();// Permite acesso ao H2 Console
                    // Endpoints públicos
                    authorize.requestMatchers(PUBLIC_PATHS).permitAll();
                    
                    // API endpoints with role-based authorization
                    authorize.requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "USER");
                    authorize.requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN");
                    authorize.requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN");
                    authorize.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN");
                    
                    // Temporarily allow all endpoints while configuring the auth-service
                    // Comment this out when auth-service is ready
                    authorize.anyRequest().permitAll(); 
                    
                    // Uncomment this when the auth-service is ready
                    // authorize.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) 
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
