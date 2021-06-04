package com.snp.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.snp.db.JdbcRepo;
import com.snp.entity.HasRole;
import com.snp.entity.Role;
import com.snp.entity.User;
import com.snp.repository.HasRoleRepository;
import com.snp.service.HasRoleService;

@Component
public class AuthProvider implements AuthenticationProvider {
	
	private static final Logger LOG =
	        Logger.getLogger(AuthProvider.class.getPackage().getName());
	
	@Autowired
	private JdbcRepo db;
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		String username = auth.getName();
		String password = (String) auth.getCredentials();
		
		User user = db.findUserByUserName(username);
        
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
        	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    		List<Role> roles = db.findRoleForUser(user.getValue());
    		//Collection<HasRole> roles = hasRole.findUserRoles(user.getValue());
    		roles.stream()
    		.forEach(x -> {
    			LOG.info("ROLES for " + username + " " + x.toString());
    			authorities.add(new SimpleGrantedAuthority("ROLE_" + x.getName()));
    		});
        	LOG.info("success returning " + authorities);
        	
    		return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }
        
    	throw new BadCredentialsException("Invalid Login... Try admin:admin");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}

}
