package com.snp.security;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.snp.db.JdbcRepo;

import jdk.internal.org.jline.utils.Log;
 
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthProvider aProv;
	
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

    	auth.authenticationProvider(aProv);
    	/*
        auth.inMemoryAuthentication()
        		.passwordEncoder(NoOpPasswordEncoder.getInstance())
                .withUser("user").password("password").roles("USER").and()
                .withUser("admin").password("password").roles("USER","ADMIN");
                */
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {    
        http.authorizeRequests()
        	.antMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated() 
            .and()
        .formLogin().and() 
        .httpBasic()
        .and()
        .logout() .invalidateHttpSession(true) 
        .clearAuthentication(true) .permitAll(); 
        http.csrf().disable();
        http.headers().frameOptions().disable();
      
    }
}