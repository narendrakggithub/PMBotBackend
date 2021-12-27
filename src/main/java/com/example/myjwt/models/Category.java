package com.example.myjwt.models;

import javax.persistence.*;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EGrade;

@Entity
@Table(name = "category")
public class Category extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20)
	private String name;
	
	public Category() {

	}

	public Category(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}