package com.katalogizegroup.katalogize.config.security;

import com.katalogizegroup.katalogize.config.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement() //Only 1 session per user.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
//                .formLogin()
//                .disable()
                .authorizeRequests()
                .antMatchers("/auth/**", "/oauth2/**", "/login")
                .permitAll();
//                .anyRequest()
//                .authenticated();

//                .and()
//                .oauth2Login()
//                .authorizationEndpoint()
//                .baseUri("oauth2/authorize")
//                .and()
//                .redirectionEndpoint()
//                .baseUri("oauth2/callback/*");
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

//        return http.oauth2ResourceServer(
//                        j -> j.jwt().jwkSetUri("http://localhost:8080/oauth2/jwks")
//                ).authorizeRequests()
//                .anyRequest().authenticated()
//                .and().build();
    }

}