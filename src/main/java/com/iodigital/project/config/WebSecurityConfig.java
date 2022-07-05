package com.iodigital.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
@EnableWebFluxSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http.csrf().disable()
//                .authorizeExchange()
//                .pathMatchers(HttpMethod.POST, "/employees/update").hasRole("ADMIN")
//                .pathMatchers("/**").permitAll()
//                .and()
//                .httpBasic();
        return http.build();
    }
}
