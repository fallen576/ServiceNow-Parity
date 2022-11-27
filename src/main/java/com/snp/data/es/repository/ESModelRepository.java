package com.snp.data.es.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.snp.data.es.model.ESModel;
import java.util.List;
import java.util.Optional;

@Repository
public interface ESModelRepository extends ElasticsearchRepository<ESModel, String> {

	Optional<ESModel> findById(String id);
	
	@Query("{\"query_string\": {\"query\": \"?0\"}}")
	List<ESModel> findByCustomQuery(String query);
}
