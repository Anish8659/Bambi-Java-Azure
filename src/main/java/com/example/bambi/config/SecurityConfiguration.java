package com.example.bambi.config;

import com.example.bambi.entity.Admin;
import com.example.bambi.repository.AdminRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private AdminRepository adminRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<Admin> adminOptional = adminRepository.findByUsername(username);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                return new User(admin.getUsername(), admin.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            } else {
                throw new UsernameNotFoundException("Admin not found");
            }
        };
    }
    public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.sendRedirect("/home");
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .requestMatchers("/register").authenticated()
                    .requestMatchers("/products").authenticated()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login?error=true")
                    .successHandler(new CustomAuthenticationSuccessHandler())
                    .permitAll()
                    .and()
                .logout()
                    .permitAll();
        return http.build();
    }
}
