package com.snp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.snp.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {

}
