package com.snp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.snp.entity.User;
import com.snp.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired(required = true)
	private UserRepository userRepo;
	
	public Iterable<User> findAll() {
		return userRepo.findAll();
	}
	
	public void save(User user) {
		System.out.println("!!!!!!!!!!!!!!!!!!!USER " + user);
		userRepo.save(user);
	}
}
