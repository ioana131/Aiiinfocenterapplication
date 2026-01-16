package org.example.aiinfocenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth

                .requestMatchers("/api/auth/**").permitAll()

                // frontend static
                .requestMatchers("/", "/index.html", "/app.js", "/style.css").permitAll()

                // 🔓 endpoint public pentru n8n / chat
                .requestMatchers("/message").permitAll()

                // 🔐 admin only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 🔐 student only
                .requestMatchers("/api/student/**").hasRole("STUDENT")

                // orice alt request necesita autentificare
                .anyRequest().authenticated()
        );

        // basic auth (email + parola)
        http.httpBasic(basic -> {});

        return http.build();
    }
}
