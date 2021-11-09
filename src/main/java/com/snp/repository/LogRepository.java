package com.snp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.snp.entity.SystemLog;

@Repository
public interface LogRepository extends CrudRepository<SystemLog, String> {

}
