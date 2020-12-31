package com.snp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.snp.entity.Module;

@Repository
public interface ModuleRepository extends CrudRepository<Module, String> {
	
}
