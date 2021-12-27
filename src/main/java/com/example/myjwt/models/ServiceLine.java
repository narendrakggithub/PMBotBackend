package com.example.myjwt.models;

import javax.persistence.*;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "serviceline")
public class ServiceLine extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private EServiceLine name;

	public ServiceLine() {

	}

	public ServiceLine(EServiceLine name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EServiceLine getName() {
		return name;
	}

	public void setName(EServiceLine name) {
		this.name = name;
	}
}