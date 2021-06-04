package com.snp.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name="sys_user_has_role")
public class HasRole {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
		name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "sys_id", updatable = false, nullable = false)
	@ColumnDefault("random_uuid()")
	@Type(type = "uuid-char")
	private UUID id;
	
	@OneToOne
	@JoinColumn(name="user")
	private User user;
	
	@OneToOne
	@JoinColumn(name="role")
	private Role role;
	
	public HasRole() {
		
	}
	
	public HasRole(Role role, User user) {
		this.role = role;
		this.user = user;
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

	@Override
	public String toString() {
		return "HasRole [id=" + id + ", user=" + user + ", role=" + role + "]";
	}
	
}
