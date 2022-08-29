package com.katalogizegroup.katalogize.config;

//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors(c -> {
//            CorsConfigurationSource source = s -> {
//                CorsConfiguration cc = new CorsConfiguration();
//                cc.setAllowCredentials(true);
//                cc.setAllowedOrigins(List.of("*"));
//                cc.setAllowedHeaders(List.of("*"));
//                cc.setAllowedMethods(List.of("*"));
//                return cc;
//            };
//
//            c.configurationSource(source);
//        });
//        return http.oauth2ResourceServer(
//                        j -> j.jwt().jwkSetUri("http://localhost:8080/oauth2/jwks")
//                ).authorizeRequests()
//                .anyRequest().authenticated()
//                .and().build();
//    }

}