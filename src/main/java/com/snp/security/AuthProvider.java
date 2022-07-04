package com.snp.security;

import java.util.ArrayList;
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
import com.snp.entity.Role;
import com.snp.entity.SystemLog;
import com.snp.entity.SystemLog.LogLevel;
import com.snp.entity.User;
import com.snp.service.LogService;

@Component
public class AuthProvider implements AuthenticationProvider {
	@Autowired
	private JdbcRepo db;
	
	@Autowired
	private LogService logService;
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		String username = auth.getName();
		String password = (String) auth.getCredentials();
		
		logService.save(new SystemLog(LogLevel.Info, "Attempting to log in " + username, "Login"));
		
		User user = db.findUserByUserName(username);
        
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
        	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    		List<Role> roles = db.findRoleForUser(user.getValue());
    		roles.stream()
    		.forEach(x -> {
    			logService.save(new SystemLog(LogLevel.Info, username + " has role ROLE_" + x.getName(), "Login"));
    			authorities.add(new SimpleGrantedAuthority("ROLE_" + x.getName()));
    		});
    		return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }
        
        logService.save(new SystemLog(LogLevel.Warning, "Failed log in for " + username, "Login"));
    	throw new BadCredentialsException("Invalid Login... Try admin:admin");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}

}
