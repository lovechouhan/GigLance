package com.minor.freelancing.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.minor.freelancing.Services.SecurityCustomUserDetailsService;

@Configuration
public class SecurityConfig {

        private final SecurityCustomUserDetailsService customUserDetailsService;

        private final OAuthenticationSuccessHandler successHandler;

        private final OAuthenticationfailureHandler failureHandler;

        public SecurityConfig(SecurityCustomUserDetailsService customUserDetailsService,
                        OAuthenticationSuccessHandler successHandler,
                        OAuthenticationfailureHandler failureHandler) {
                this.customUserDetailsService = customUserDetailsService;
                this.successHandler = successHandler;
                this.failureHandler = failureHandler;
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(customUserDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/login", "/register", "/register/**", "/css/**",
                                                                "/js/**", "/oauth2/**", "/",
                                                                "/images/**")
                                                .permitAll()
                                                .requestMatchers("/client/**").hasRole("CLIENT")
                                                .requestMatchers("/freelancer/reviews/**").authenticated()
                                                .requestMatchers("/freelancer/**").hasRole("FREELANCER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/authenticate")
                                                .successHandler(successHandler)
                                                .failureHandler(failureHandler)
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .permitAll()

                                )
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true"))
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .successHandler(successHandler))
                                .csrf(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
                return builder.getAuthenticationManager();
        }

}
