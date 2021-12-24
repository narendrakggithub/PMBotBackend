package com.example.myjwt.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;

@Entity
@Table(name = "sbu")
public class Sbu extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String sbuName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sbuHeadId")
	User sbuHead;

	private Boolean isActive;

	public Sbu() {

	}

	public Sbu(String sbuName) {
		this.sbuName = sbuName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSbuName() {
		return sbuName;
	}

	public void setSbuName(String sbuName) {
		this.sbuName = sbuName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public User getSbuHead() {
		return sbuHead;
	}

	public void setSbuHead(User sbuHead) {
		this.sbuHead = sbuHead;
	}
}