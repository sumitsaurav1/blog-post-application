package com.sumit.blogpostapp.security;
import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.Collection;

@Configuration
public class SecurityConfig
{
    @Bean
    public UserDetailsManager userDetailsManager (DataSource dataSource){
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "SELECT email, password,active from users where email =?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select email, role from users where email =?");
        return jdbcUserDetailsManager;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(configurer ->
                configurer
//                        .requestMatchers("/","/posts/{id}","/filter","/search","/resister").permitAll()
                        .requestMatchers("/posts/{id}/edit","/posts/{id}/delete",
                         "/see/drafts","/posts/{id}/publish","/myPost").hasAnyRole("Admin","Author")
                        .requestMatchers("/posts/new").authenticated()
                        .anyRequest().permitAll()
        )
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateTheUser").permitAll())
                .logout(logout ->logout.permitAll());
               
        return http.build();
    }
}




