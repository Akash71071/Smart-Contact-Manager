package com.cm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MyConfig 
{
   @Autowired
   public AuthenticationSuccessHandler CustomSuccessHandler;
   @Bean
   public UserDetailsService getUserDetailsService()
   {
	   return new UserDetailsServiceImple();
   }
   
   @Bean
   public BCryptPasswordEncoder passwordEncoder()
   {
	   return new BCryptPasswordEncoder();
   }
   
   @Bean
   public AuthenticationProvider authenticationProvider()
   {
	   DaoAuthenticationProvider daoauthenticationProvider = new DaoAuthenticationProvider();
	   daoauthenticationProvider.setUserDetailsService(getUserDetailsService());
	   daoauthenticationProvider.setPasswordEncoder(passwordEncoder());
	   
	   return daoauthenticationProvider;
   }
   
   //Configure method
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
   {
	 return http.authorizeHttpRequests()
	        .requestMatchers("/**").permitAll()
	        .and()
	        .authorizeHttpRequests().requestMatchers("/user/**").hasRole("USER")
	        .requestMatchers("/admin/**").hasRole("ADMIN")
	        .anyRequest().authenticated()
	        .and().formLogin()
	        .loginPage("/signin")
	        .loginProcessingUrl("/dologin")
	        .successHandler(CustomSuccessHandler)
	        .and().csrf().disable().build();
   }
   
   
}
