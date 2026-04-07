package com.smartcampus.it3030_paf_2026_smart_campus.config;

import com.smartcampus.it3030_paf_2026_smart_campus.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.resource-admin.username:admin}")
    private String resourceAdminUsername;

    @Value("${app.resource-admin.password:123456}")
    private String resourceAdminPassword;

    @Bean
    public UserDetails adminUserDetails(PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(resourceAdminUsername)
                .password(passwordEncoder.encode(resourceAdminPassword))
                .roles("ADMIN")
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/resources", "/api/resources/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/resources").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/resources/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/resources/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserDetails adminUserDetails, AppUserRepository appUserRepository) {
        return username -> {
            String normalized = username == null ? "" : username.trim().toLowerCase();
            if (normalized.equals(adminUserDetails.getUsername().trim().toLowerCase())) {
                return adminUserDetails;
            }
            return appUserRepository.findByEmail(normalized)
                    .map(user -> User.builder()
                            .username(user.getEmail())
                            .password(user.getPasswordHash())
                            .roles("USER")
                            .build())
                    .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
