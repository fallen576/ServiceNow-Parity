package com.snp.service;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.snp.entity.User;
import com.snp.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired(required = true)
	private UserRepository userRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	public Iterable<User> findAll() {
		return userRepo.findAll();
	}
	
	public void save(User user) {
		userRepo.save(user);
	}	
}
