package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "wsrupdate")
public class WsrUpdate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	private Date updatedOn;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedBy")
	User updatedBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wsrId")
	Wsr wsr;


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


	public Wsr getWsr() {
		return wsr;
	}


	public void setWsr(Wsr wsr) {
		this.wsr = wsr;
	}


	public WsrUpdate() {
	
	}
}