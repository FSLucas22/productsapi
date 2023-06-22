package com.api.products.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableMethodSecurity
//@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         UserDetailsServiceImpl userService) throws Exception {
        http.userDetailsService(userService)
                .headers(headers -> headers.frameOptions(options -> options.sameOrigin()))
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
