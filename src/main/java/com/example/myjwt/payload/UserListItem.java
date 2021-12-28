package com.example.myjwt.payload;

import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.EGrade;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public class UserListItem {
	private Long id;
	private String userName;
	private String grade;
	private String userFullName;
	private String accountName;
	private String projectName;

	public UserListItem(User user) {
		this.id = user.getId();
		this.userName = user.getUserName();
		this.userFullName = user.getFullName();
		if (user.getGrade() != null)
			this.grade = user.getGrade().getName().name();
	}
	
	public UserListItem(NativeQueryUser user) {
		this.id = user.getId();
		this.userName = user.getUserName();
		this.userFullName = user.getFullName();
		int index = (int)(user.getGradeId()).longValue();
		this.grade = (EGrade.values()[index-1]).name();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
