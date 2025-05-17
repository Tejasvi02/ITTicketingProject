package com.synex.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private UserDetailsService userDetailsService; //imported from spring security

	@Bean
	static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean 
	public SecurityFilterChain apiFilterChain2(HttpSecurity http) throws Exception {
		http
			.apply(MyCustomDsl.customDsl())
			.flag(true).and()
			.authorizeRequests()
				.requestMatchers("/register", "/css/**", "/js/**", "/login", "/logout-success").permitAll().and()
			.authorizeRequests()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/manager/**").hasRole("MANAGER")
				.requestMatchers("/user/**").hasRole("USER").and()
			.formLogin()
			.loginPage("/login")
			.failureUrl("/login?error=true")
			.defaultSuccessUrl("/home",true).permitAll().and()
		.logout()
		.logoutSuccessUrl("/logout-success")
		.invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .permitAll();
		
		return http.build();
	}


}
