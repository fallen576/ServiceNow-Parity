package com.snp.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.snp.security.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snp.data.es.model.ESLog;
import com.snp.data.es.repository.ESLogRepository;
import com.snp.entity.SystemLog;
import com.snp.repository.LogRepository;

@Service
public class LogService {
	
	@Autowired(required = true)
	private LogRepository logRepo;
	
	@Autowired(required = true)
	private ESLogRepository esLogRepo;
	
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
		String user = auth.getAuthentication().getName();
		SystemLog log = new SystemLog(SystemLog.LogLevel.Info, message, source, user);
		logRepo.save(log);
		
		ESLog esLog = new ESLog(message, SystemLog.LogLevel.Info.toString(), source, user);
		esLogRepo.save(esLog);
	}

	public void error(String message, String source) {
		String user = auth.getAuthentication().getName();
		SystemLog log = new SystemLog(SystemLog.LogLevel.Error, message, source,  user);
		logRepo.save(log);
		
		ESLog esLog = new ESLog(message, SystemLog.LogLevel.Info.toString(), source, user);
		esLogRepo.save(esLog);
	}
	
	public void warn(String message, String source) {
		String user = auth.getAuthentication().getName();
		SystemLog log = new SystemLog(SystemLog.LogLevel.Warning, message, source, user);
		logRepo.save(log);
		
		ESLog esLog = new ESLog(message, SystemLog.LogLevel.Info.toString(), source, user);
		esLogRepo.save(esLog);
	}
}
