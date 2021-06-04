package com.snp.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.snp.entity.Role;
import com.snp.repository.RoleRepository;

@Service
public class RoleService {

	@Autowired(required = true)
	private RoleRepository roleRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	public Iterable<Role> findAll() {
		return roleRepo.findAll();
	}
	
	public void save(Role role) {
		roleRepo.save(role);
	}
}
