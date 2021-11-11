package com.snp.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import com.snp.entity.HasRole;
import com.snp.entity.Role;
import com.snp.entity.User;
import com.snp.entity.ListLayout;
import com.snp.repository.HasRoleRepository;
import com.snp.repository.UserPreference;

@Service
public class UserPreferenceService {
	
	@Autowired(required = true)
	private UserPreference userPref;
	
	@PersistenceContext
	private EntityManager em;
	
	public Iterable<ListLayout> findAll() {
		return userPref.findAll();
	}
	
	public void save(ListLayout listLayout) {
		userPref.save(listLayout);
	}
	
	public ListLayout findByTableAndUser(String table, User user) {
		return userPref.findByTableAndUser(table, user);
	}
	
}
