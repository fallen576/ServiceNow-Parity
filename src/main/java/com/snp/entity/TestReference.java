package com.snp.entity;

import java.util.Date;
import java.util.UUID;

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

@Entity
@Table(name="example_reference")
public class TestReference {
	
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
	
	@Column(name = "sys_created_on", updatable = false)
	public Date createdOn;

	@Column(name = "sys_updated_on", updatable = false)
	public Date updatedOn;
	
	@PrePersist
	void createdAt() {
		this.createdOn = this.updatedOn = new Date();
	}

	@PreUpdate
	void updatedAt() {
		this.updatedOn = new Date();
	}
	
	public TestReference() {
		
	}
	
	public TestReference(User user) {
		this.user = user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
}
