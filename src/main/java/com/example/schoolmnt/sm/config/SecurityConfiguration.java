package com.example.schoolmnt.sm.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import java.io.IOException;

import static com.example.schoolmnt.sm.appuser.Role.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    private final JwtService jwtService;

    private static final String[] WHITE_LIST_URL = {
            "/register",
            "/registration/confirm"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->req.requestMatchers(WHITE_LIST_URL)
                        .permitAll()
                        .requestMatchers(GET,"/teacher/**").hasAnyRole(ADMIN.name(), TEACHER.name(), STUDENT.name())
                        .requestMatchers("/teacher/**").hasAnyRole(ADMIN.name(), TEACHER.name())
                        .requestMatchers(GET,"/classes").hasAnyRole(ADMIN.name(), TEACHER.name(), STUDENT.name())
                        .requestMatchers(GET,"/class/**").hasAnyRole(ADMIN.name(), TEACHER.name(), STUDENT.name())
                        .requestMatchers("/class/**").hasAnyRole(ADMIN.name(), TEACHER.name())
                        .requestMatchers("/students/**").hasAnyRole(ADMIN.name(), TEACHER.name())
                        .requestMatchers(GET,"/exams").hasAnyRole(ADMIN.name(), TEACHER.name(), STUDENT.name())
                        .requestMatchers("/exam/**").hasAnyRole(ADMIN.name(), TEACHER.name())
                        .requestMatchers(GET,"/post/detail/**").hasAnyRole(ADMIN.name(), TEACHER.name(), STUDENT.name())
                        .requestMatchers("/post/**").hasAnyRole(ADMIN.name(), TEACHER.name())
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                String jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());
                                Cookie jwtCookie = new Cookie("token", jwtToken);
                                jwtCookie.setHttpOnly(true);
                                jwtCookie.setSecure(true);
                                jwtCookie.setMaxAge(60*60*24);
                                response.addCookie(jwtCookie);
                                response.sendRedirect(request.getContextPath() + "/");
                            }
                        })
                        .permitAll())
                .logout(
                        (logout) -> logout.addLogoutHandler( new CookieClearingLogoutHandler("token"))
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
