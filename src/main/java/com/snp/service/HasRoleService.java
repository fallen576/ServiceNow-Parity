package com.snp.service;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import com.snp.entity.HasRole;
import com.snp.entity.Role;
import com.snp.repository.HasRoleRepository;

@Service
public class HasRoleService {
	
	@Autowired(required = true)
	private HasRoleRepository hasRoleRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	public Iterable<HasRole> findAll() {
		return hasRoleRepo.findAll();
	}
	
	public void save(HasRole hasRole) {
		hasRoleRepo.save(hasRole);
	}
}
