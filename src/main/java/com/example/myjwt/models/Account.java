package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;

@Entity
@Table(name = "account")
public class Account extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String accountName;

	private Boolean isActive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "edlId")
	User edl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pdlId")
	User pdl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sbuId")
	Sbu sbu;

	public Account() {

	}

	public Account(String accountName) {
		this.accountName = accountName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}


	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public User getEdl() {
		return edl;
	}

	public void setEdl(User edl) {
		this.edl = edl;
	}

	public User getPdl() {
		return pdl;
	}

	public void setPdl(User pdl) {
		this.pdl = pdl;
	}

	public Sbu getSbu() {
		return sbu;
	}

	public void setSbu(Sbu sbu) {
		this.sbu = sbu;
	}

}