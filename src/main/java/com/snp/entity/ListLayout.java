package com.snp.entity;

import java.util.List;
import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="sys_user_preference", uniqueConstraints = {@UniqueConstraint(columnNames = {"table", "user_ref"})})
public class ListLayout extends BaseTable {

	public ListLayout() {
		super();
	}
	
	public ListLayout(String username) {
		super(username);
	}
	
	public ListLayout(String table, String[] list, User user) {
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
	
	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sys_user_field", joinColumns = @JoinColumn(name = "name"))
	@JoinColumn(name = "name") 
    @Column(name = "list")
	@OrderColumn(name="list_order")
	@OnDelete(action= OnDeleteAction.CASCADE)
    public String[] list;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String[] getList() {
		return list;
	}

	public void setList(String[] list) {
		this.list = list;
	}
}
