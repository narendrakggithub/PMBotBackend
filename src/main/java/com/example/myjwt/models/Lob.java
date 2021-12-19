package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "lob")
public class Lob {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String lobName;

	private Boolean isActive;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accountId")
	Account account;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	User owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdBy")
	User createdBy;

	public Lob() {

	}

	public Lob(String lobName) {
		this.lobName = lobName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLobName() {
		return lobName;
	}

	public void setLobName(String lobName) {
		this.lobName = lobName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}


	


}