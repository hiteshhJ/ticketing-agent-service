package com.sainsburys.agent.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private static final String ROLE_CLAIM = "roles";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                jwt -> {
                    Collection<GrantedAuthority> authorities =
                            new JwtGrantedAuthoritiesConverter().convert(jwt);

                    Object rolesClaim = jwt.getClaim(ROLE_CLAIM);

                    if (rolesClaim == null) {
                        log.warn(
                                "JWT Bearer Token - No roles claim found. Available claims: {}",
                                jwt.getClaims().keySet());
                        return authorities;
                    }

                    HashSet<GrantedAuthority> grantedAuthorities = new HashSet<>(authorities);
                    if (rolesClaim instanceof List<?> rolesList) {
                        for (Object role : rolesList) {
                            if (role instanceof String roleStr) {
                                grantedAuthorities.add(new SimpleGrantedAuthority(roleStr));
                            }
                        }
                    } else if (rolesClaim instanceof String singleRole) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(singleRole));
                    }
                    return grantedAuthorities;
                });
        return jwtAuthenticationConverter;
    }
}
