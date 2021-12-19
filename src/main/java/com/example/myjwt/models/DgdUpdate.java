package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "dgdupdate")
public class DgdUpdate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private Date updatedOn;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedBy")
	User updatedBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dgdId")
	Dgd dgd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Dgd getDgd() {
		return dgd;
	}

	public void setDgd(Dgd dgd) {
		this.dgd = dgd;
	}

	public DgdUpdate() {
	
	}
}