package com.snp.security;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.snp.db.JdbcRepo;
import com.snp.entity.User;

@Component
public class AuthProvider implements AuthenticationProvider {
	
	private static final Logger LOG =
	        Logger.getLogger(AuthProvider.class.getPackage().getName());
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		LOG.info("Inside AuthProvider Class " + auth.getName() + " " + auth.getCredentials());
		String username = auth.getName();
		String password = (String) auth.getCredentials();
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        //authorities.add(new SimpleGrantedAuthority("ROLE_ONE"));
        authorities.add(new SimpleGrantedAuthority("ROLE_anyone"));
		User user = new User(username, username, username);
		return new UsernamePasswordAuthenticationToken(username, password, authorities);
		
		
		//if good return auth else return null??
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}

}
