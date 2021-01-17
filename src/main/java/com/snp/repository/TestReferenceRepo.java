package com.snp.repository;

import com.snp.entity.TestReference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestReferenceRepo extends CrudRepository<TestReference, String> {
	
}
