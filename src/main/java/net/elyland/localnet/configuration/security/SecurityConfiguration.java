package net.elyland.localnet.configuration.security;

import net.elyland.localnet.configuration.repository.RepositoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * Created by Igor on 09-Jun-16.
 */
@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@AutoConfigureAfter({RepositoryConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {
        "net.elyland.localnet",
        "net.elyland.localnet.controllers",
        "net.elyland.localnet.domains",
        "net.elyland.localnet.repositories",
        "net.elyland.localnet.services",
        "net.elyland.localnet.validators"})

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Autowired
    AccessDeniedHandler accessDeniedHandler;
    @Autowired
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http    .httpBasic().and()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/admin/**").access("hasAuthority('ADMIN')")
//                .antMatchers("/api/tester/**").access("hasAuthority('TESTER')")
                .antMatchers(
                        "/**",
                        "/logon/**",
                        "/login")
                .permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        http.formLogin()
//                .failureUrl("/logon/login_error")
                .successHandler(customAuthenticationSuccessHandler)
//                .defaultSuccessUrl("/logon/success_login")
                .failureHandler(loginFailureHandler)
//                .loginPage("/logon/login")
                .usernameParameter("username").passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/logon/success_logout")
                .permitAll()
                .and().exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);
    }

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // you USUALLY want this
//        config.addAllowedOrigin("http://192.168.0.219:4200");
        config.addAllowedOrigin("*");
        config.setMaxAge((long)3600);
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder);

    }

}