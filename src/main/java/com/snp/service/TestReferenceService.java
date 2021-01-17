package com.snp.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snp.entity.TestReference;
import com.snp.entity.User;
import com.snp.repository.TestReferenceRepo;

@Service
public class TestReferenceService {
	
	@Autowired(required = true)
	private TestReferenceRepo tfRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	public void save(TestReference testReference) {
		tfRepo.save(testReference);
	}	
}
