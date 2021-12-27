package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "resource")
public class Resource extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "projectId")
	Project project;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceLineId")
	ServiceLine serviceLine;
	
	private String soId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associateId")
	User associate;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statusId")
	Status status;
	
	private String location;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plannedGradeId")
	Grade plannedGrade;
	
	private String projectRole;
	
	@NotBlank
	private Date plannedStartDate;
	
	@NotBlank
	private Date tentativeStartDate;
	
	private Boolean isActive;


	public Resource() {
		
	}
	
	public Resource(Project project) {
		this.project = project;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}


	public ServiceLine getServiceLine() {
		return serviceLine;
	}


	public void setServiceLine(ServiceLine serviceLine) {
		this.serviceLine = serviceLine;
	}


	public String getSoId() {
		return soId;
	}


	public void setSoId(String soId) {
		this.soId = soId;
	}


	public User getAssociate() {
		return associate;
	}


	public void setAssociate(User associate) {
		this.associate = associate;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public Grade getPlannedGrade() {
		return plannedGrade;
	}


	public void setPlannedGrade(Grade plannedGrade) {
		this.plannedGrade = plannedGrade;
	}


	public String getProjectRole() {
		return projectRole;
	}


	public void setProjectRole(String projectRole) {
		this.projectRole = projectRole;
	}


	public Date getPlannedStartDate() {
		return plannedStartDate;
	}


	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}


	public Date getTentativeStartDate() {
		return tentativeStartDate;
	}


	public void setTentativeStartDate(Date tentativeStartDate) {
		this.tentativeStartDate = tentativeStartDate;
	}


	public Boolean getIsActive() {
		return isActive;
	}


	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}



}