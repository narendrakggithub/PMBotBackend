package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "action")
public class Action extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
	Category category;
	
	@NotBlank
	@Size(max = 200)
	private String actionDesc;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId")
	User owner;
	
	@NotBlank
	private Date createDate;
	
	@NotBlank
	private Date eta;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "statusId")
	Status status;

	@NotBlank
	@Size(max = 200)
	private String remarks;
	
	private Boolean isActive;

	public Action() {

	}
	
	public Action(String actionDesc) {
		this.actionDesc = actionDesc;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}


	public String getActionDesc() {
		return actionDesc;
	}


	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}


	public User getOwner() {
		return owner;
	}


	public void setOwner(User owner) {
		this.owner = owner;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Date getEta() {
		return eta;
	}


	public void setEta(Date eta) {
		this.eta = eta;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public Boolean getIsActive() {
		return isActive;
	}


	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}



}