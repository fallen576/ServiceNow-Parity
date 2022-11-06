package com.snp.data.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.snp.data.es.model.ESLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;

@Repository
public interface ESLogRepository extends ElasticsearchRepository<ESLog, String> {

	Page<ESLog> findByText(String text, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<ESLog> findByLogMessageUsingCustomQuery(String name, Pageable pageable);

    @Query("{\"bool\": {\"must\": {\"match_all\": {}}, \"filter\": {\"term\": {\"tags\": \"?0\" }}}}")
    Page<ESLog> findByFilteredTagQuery(String tag, Pageable pageable);

    @Query("{\"bool\": {\"must\": {\"match\": {\"authors.name\": \"?0\"}}, \"filter\": {\"term\": {\"tags\": \"?1\" }}}}")
    Page<ESLog> findByLogMessageAndFilteredTagQuery(String name, String tag, Pageable pageable);
    
}
