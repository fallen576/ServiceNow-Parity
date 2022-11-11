package com.snp.data.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.snp.data.es.model.ESModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ESModelRepository extends ElasticsearchRepository<ESModel, String> {

	Page<ESModel> findById(String id, Pageable pageable);
	
}
