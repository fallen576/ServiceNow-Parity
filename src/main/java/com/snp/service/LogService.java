package com.snp.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.snp.security.IAuthenticationFacade;
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

	@Autowired
	private IAuthenticationFacade auth;
	
	public Iterable<SystemLog> findAll() {
		return logRepo.findAll();
	}
	
	public void save(SystemLog log) {
		logRepo.save(log);
	}

	public void info(String message, String source) {
		SystemLog log = new SystemLog(SystemLog.LogLevel.Info, message, source, auth.getAuthentication().getName());
		logRepo.save(log);
	}

	public void error(String message, String source) {
		SystemLog log = new SystemLog(SystemLog.LogLevel.Error, message, source, auth.getAuthentication().getName());
		logRepo.save(log);
	}
}
