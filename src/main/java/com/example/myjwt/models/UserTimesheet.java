package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "usertimesheet")
public class UserTimesheet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associateId")
	User associate;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
	User project;
	
	@NotBlank
	private Date month;
	
	@NotBlank
	private Date year;
	
	private String[] effort = new String[31]; 
	
	@NotBlank
	private Date updatedOn;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedBy")
	User updatedBy;
	
	public UserTimesheet(User associate, User project) {
		this.associate = associate;
		this.project = project;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAssociate() {
		return associate;
	}

	public void setAssociate(User associate) {
		this.associate = associate;
	}

	public User getProject() {
		return project;
	}

	public void setProject(User project) {
		this.project = project;
	}

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public String[] getEffort() {
		return effort;
	}

	public void setEffort(String[] effort) {
		this.effort = effort;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}



}