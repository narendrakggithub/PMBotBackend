package com.example.myjwt.payload.request;

import java.sql.Date;
import java.util.Set;

import javax.validation.constraints.*;
 
public class CreateProjectRequest {
    @NotBlank
    @Size(min = 10, max = 50)
    private String projectName;
    
    @NotBlank
    private Date startDate;
    
    @NotBlank
    private Date endDate;
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String pmName;
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String subLobName;
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String customerName;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPmName() {
		return pmName;
	}

	public void setPmName(String pmName) {
		this.pmName = pmName;
	}

	public String getSubLobName() {
		return subLobName;
	}

	public void setSubLobName(String subLobName) {
		this.subLobName = subLobName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
    
}
