package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "risk")
public class Risk extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 50)
	private String riskName;

	@NotBlank
	@Size(max = 200)
	private String riskDesc;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "projectId")
	Project project;


	@NotBlank
	private Date eta;

	@NotBlank
	@Size(max = 200)
	private String mitigationPlan;

	private Boolean isActive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	User owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "statusId")
	Status status;


	public Risk() {

	}

	public Risk(String riskName) {
		this.riskName = riskName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRiskName() {
		return riskName;
	}

	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}

	public String getRiskDesc() {
		return riskDesc;
	}

	public void setRiskDesc(String riskDesc) {
		this.riskDesc = riskDesc;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}



	public Date getEta() {
		return eta;
	}

	public void setEta(Date eta) {
		this.eta = eta;
	}

	public String getMitigationPlan() {
		return mitigationPlan;
	}

	public void setMitigationPlan(String mitigationPlan) {
		this.mitigationPlan = mitigationPlan;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}



}