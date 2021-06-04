package com.snp.service;


import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.snp.db.JdbcRepo;
import com.snp.entity.User;
import com.snp.repository.UserRepository;

import jdk.internal.org.jline.utils.Log;

@Service
public class UserService implements UserDetailsService {
	
	private static final Logger LOG =
	        Logger.getLogger(UserService.class.getPackage().getName());
	
	@Autowired(required = true)
	private UserRepository userRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private JdbcRepo db;
	
	public Iterable<User> findAll() {
		return userRepo.findAll();
	}
	
	public void save(User user) {
		userRepo.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOG.info("!!!!!!!! in loadUserByName");
		User user = db.findUserByUserName(username);
		LOG.info("!!!!!!!! found user " + user);
		
		if (user == null) {
			throw new UsernameNotFoundException("Username " + username + " not found.");
		}
		return (UserDetails) user; 
	}	
}
