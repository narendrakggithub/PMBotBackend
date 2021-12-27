package com.example.myjwt.models;

import java.sql.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.UserDateAudit;
import com.example.myjwt.models.enm.EServiceLine;

@Entity
@Table(name = "dgdupdate")
public class DgdUpdate extends UserDateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dgdId")
	Dgd dgd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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