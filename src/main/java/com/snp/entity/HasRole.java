package com.snp.entity;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;

import com.snp.db.JdbcRepo;
import com.snp.security.IAuthenticationFacade;

@Entity
@Table(name = "sys_user_has_role")
public class HasRole {
	
	private static final Logger LOG =
	        Logger.getLogger(JdbcRepo.class.getPackage().getName());

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "sys_id", updatable = false, nullable = false)
	@ColumnDefault("random_uuid()")
	@Type(type = "uuid-char")
	private UUID id;

	@Column(name = "sys_created_on", updatable = false)
	public Date createdOn;

	@Column(name = "sys_updated_on", updatable = false)
	public Date updatedOn;

	@Column(name = "sys_created_by")
	public String sys_created_by;
	
	@Column(name = "sys_updated_by")
	public String sys_updated_by;
	
	@OneToOne
	@JoinColumn(name = "user")
	private User user;

	@OneToOne
	@JoinColumn(name = "role")
	private Role role;

	public HasRole() {

	}

	public HasRole(Role role, User user) {
		this.role = role;
		this.user = user;
	}
	
	public HasRole(Role role, User user, String name) {
		this.role = role;
		this.user = user;
		this.sys_created_by = name;
		this.sys_updated_by = name;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getSys_created_by() {
		return sys_created_by;
	}

	public void setSys_created_by(String name) {
		this.sys_created_by = name;
	}

	public void setSys_updated_by(String name) {
		this.sys_updated_by = name;
	}

	public String getSys_updated_by() {
		return sys_updated_by;
	}

	@PrePersist
	void createdAt() {
		this.createdOn = this.updatedOn = new Date();
	}

	@PreUpdate
	void updatedAt() {
		this.updatedOn = new Date();
	}

	@Override
	public String toString() {
		return "HasRole [id=" + id + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", user=" + user
				+ ", role=" + role + "]";
	}

}
