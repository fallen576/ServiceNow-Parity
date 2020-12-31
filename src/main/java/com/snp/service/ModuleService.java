package com.snp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snp.entity.Module;
import com.snp.repository.ModuleRepository;

@Service
public class ModuleService {
	
	@Autowired(required = true)
	private ModuleRepository moduleRepo;
	
	public Iterable<Module> findAll() {
		return moduleRepo.findAll();
	}
	
	public void save(Module module) {
		moduleRepo.save(module);
	}
}

