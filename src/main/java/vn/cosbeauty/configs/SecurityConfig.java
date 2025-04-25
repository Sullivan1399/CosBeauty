package vn.cosbeauty.configs;

import org.springframework.security.core.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.core.GrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                		"/",
                		"/web/**",
                		"/register", 
                		"/verify", 
                		"/login", 
                		"/css/**", 
                		"/js/**", 
                		"/fonts/**",
                        "/images/**",
                        "/img/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                .requestMatchers("/customer/**").hasRole( "CUSTOMER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
					.loginPage("/login")
					.successHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
						// Ưu tiên xử lý ADMIN
						for (GrantedAuthority authority : authentication.getAuthorities()) {
							String role = authority.getAuthority();
							if (role.equals("ROLE_ADMIN")) {
								response.sendRedirect("/admin/accounts");
								return;
							} else if (role.equals("ROLE_EMPLOYEE")) {
								response.sendRedirect("/employee/import-orders");
								return;
							}
						}

						response.sendRedirect("/");
					})
					.failureUrl("/login?error=true")
					.permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .permitAll()
            );
        return http.build();
    }
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	    return authenticationConfiguration.getAuthenticationManager();
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }

}