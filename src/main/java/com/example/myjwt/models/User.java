package com.example.myjwt.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.myjwt.models.audit.DateAudit;

@Entity
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "userName"),
		@UniqueConstraint(columnNames = "email") })
public class User extends DateAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String userName;

	@NotBlank
	@Size(max = 50)
	private String fullName;
	
	@NotBlank
	@Size(max = 50)
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;

	private Boolean isVerified;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerId")
	User manager;
	
	private Boolean isActive;
	
	private Boolean isApproved;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradeId")
	private Grade grade;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleid")
	private Role role;

	public User() {
	}

	public User(String userName, String email, String password, String verificationCode, boolean isVerified) {
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.isVerified=isVerified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}



	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public User getManager() {
		return manager;
	}

	public void setManager(User manager) {
		this.manager = manager;
	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	
	
}
