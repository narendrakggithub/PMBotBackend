package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "userproject")
public class UserProject {
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
	private Date startDate;
	
	@NotBlank
	private Date endDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
	User createdBy;
	
	public UserProject() {

	}

	public UserProject(User associate, User project) {
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



	public Date getStartDate() {
		return startDate;
	}



	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}



	public Date getEndDate() {
		return endDate;
	}



	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public User getCreatedBy() {
		return createdBy;
	}



	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}



}