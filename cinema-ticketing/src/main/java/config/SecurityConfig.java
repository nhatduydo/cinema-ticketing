package config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Đánh dấu class là file cấu hình của Spring
@EnableWebSecurity // Kích hoạt hệ thống bảo mật Cho phép bạn custom:Filter, Authentication, Authorization
@EnableMethodSecurity // Bật bảo mật ngay trên method (Bật check quyền theo method)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
//@Component: Spring tự tạo
// @Bean: Bạn tạo ra, Spring quản lý, Dùng khi:
//    +  1. Class không phải của bạn
//    +  2. Cần custom object
//    + 3. Cấu hình phức tạp
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Những API không cần đăng nhập
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/movies/**").permitAll()
                    .requestMatchers("/api/showtimes/**").permitAll()
                    .requestMatchers("/uploads/**").permitAll()
                    // API admin cần role ADMIN
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    // Các API khác cần đăng nhập
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
