package com.snp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.snp.entity.ListLayout;

@Repository
public interface UserPreference extends CrudRepository<ListLayout, String> {

}
