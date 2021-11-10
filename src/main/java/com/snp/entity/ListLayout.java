package com.snp.entity;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name="sys_user_preference")
public class ListLayout extends BaseTable {

	public ListLayout() {
		super();
	}
	
	public ListLayout(String username) {
		super(username);
	}
	
	public ListLayout(String table, List<String> list, User user) {
		super();
		this.table = table;
		this.user = user;
		this.list = list;
	}
	
	@Column(name="table")
	public String table;
	
	@OneToOne
	@JoinColumn(name="user_ref")
	private User user;
	
	@ElementCollection
    @CollectionTable(name = "sys_user_field", joinColumns = @JoinColumn(name = "name"))
    @Column(name = "list")
    public List<String> list;
	
}
