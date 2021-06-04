package com.snp.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.snp.entity.HasRole;
import com.snp.entity.Role;

@Repository
public interface HasRoleRepository extends CrudRepository<HasRole, String> {

}
