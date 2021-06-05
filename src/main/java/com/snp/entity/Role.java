package com.snp.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name="sys_user_role")
public class Role {
	
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
	
	@Column(name="name")
	private String name;
	
	@Column(name="description")
	private String description;
	
	@Column(name = "sys_created_on", updatable = false)
	public Date createdOn;

	@Column(name = "sys_updated_on", updatable = false)
	public Date updatedOn;
	
	public Role() {
		
	}
	
	public Role(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		return "Role [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
	
}
