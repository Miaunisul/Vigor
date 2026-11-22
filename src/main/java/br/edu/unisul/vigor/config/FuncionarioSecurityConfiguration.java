package br.edu.unisul.vigor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class FuncionarioSecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            FuncionarioAuthenticationProvider funcionarioAuthenticationProvider)
            throws Exception {
        http
                .authenticationProvider(funcionarioAuthenticationProvider)
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                "/login", "/css/**", "/images/**", "/js/**", "/favicon.ico")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(formulario -> formulario
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("login")
                        .passwordParameter("senha")
                        .defaultSuccessUrl("/membros", true)
                        .failureUrl("/login?erro"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?sair")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement(sessao -> sessao
                        .sessionFixation(fixacao -> fixacao.changeSessionId()))
                .csrf(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
