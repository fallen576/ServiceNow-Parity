package com.snp.data.es;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.snp.data.es.repository")
@ComponentScan(basePackages = { "com.snp.data.es.service" })
public class Config {
	@Bean
    RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        		/*
        		 * 
        		 * docker network create snp
        		 * docker run -d --name es --net snp -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.17.7
        		 * docker run -d --name snpWeb --net snp -p 8080:8080 -p 9990:9990 fallen576/snp:latest
        		 * 
        		 * OR
        		 * 
        		 * docker compose up
        		 * 
        		 */
            .connectedTo("es:9200")
            .build();

        return RestClients.create(clientConfiguration)
            .rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
