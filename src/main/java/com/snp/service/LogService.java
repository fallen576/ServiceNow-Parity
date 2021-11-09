package com.snp.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.snp.entity.SystemLog;
import com.snp.repository.LogRepository;

@Service
public class LogService {
	
	@Autowired(required = true)
	private LogRepository logRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	public Iterable<SystemLog> findAll() {
		return logRepo.findAll();
	}
	
	public void save(SystemLog log) {
		logRepo.save(log);
	}
}
