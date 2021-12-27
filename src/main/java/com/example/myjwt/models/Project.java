package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "project")
public class Project extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 20)
	private String projectName;
	
	@NotBlank
	private Date startDate;
	
	@NotBlank
	private Date endDate;
	
	private Boolean isActive;

	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pmId")
	User projectManager;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subLobId")
	SubLob subLob;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
	Customer customer;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}


	public User getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(User projectManager) {
		this.projectManager = projectManager;
	}

	public SubLob getSubLob() {
		return subLob;
	}

	public void setSubLob(SubLob subLob) {
		this.subLob = subLob;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Project() {

	}

	public Project(String projectName) {
		this.projectName = projectName;
	}

}