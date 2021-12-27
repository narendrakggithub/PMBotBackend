package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "wsrupdate")
public class WsrUpdate extends UserDateAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wsrId")
	Wsr wsr;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
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