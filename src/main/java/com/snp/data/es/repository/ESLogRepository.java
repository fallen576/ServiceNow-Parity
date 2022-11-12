package com.snp.data.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.snp.data.es.model.ESLog;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;

@Repository
public interface ESLogRepository extends ElasticsearchRepository<ESLog, String> {

	@Query("{\"match_phrase\": {\"text\": {\"query\": \"?0\"}}}")
	List<ESLog> findByText(String text);
	
	List<ESLog> findByTextLike(String text);
	List<ESLog> findByTextContaining(String text);
	
	/*
	 * {"query": {
    		"query_string" : {"default_field" : "name", "query" : "*oh* *oe*"}
		} }
	 * */
	@Query("{\"query_string\": {\"default_field\": \"text\", \"query\": \"?0\"}}")
	List<ESLog> findByCustom(String query);
	
    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<ESLog> findByLogMessageUsingCustomQuery(String name, Pageable pageable);

    @Query("{\"bool\": {\"must\": {\"match_all\": {}}, \"filter\": {\"term\": {\"tags\": \"?0\" }}}}")
    Page<ESLog> findByFilteredTagQuery(String tag, Pageable pageable);

    @Query("{\"bool\": {\"must\": {\"match\": {\"authors.name\": \"?0\"}}, \"filter\": {\"term\": {\"tags\": \"?1\" }}}}")
    Page<ESLog> findByLogMessageAndFilteredTagQuery(String name, String tag, Pageable pageable);
    
}
