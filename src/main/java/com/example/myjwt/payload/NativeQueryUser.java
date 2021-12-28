package com.example.myjwt.payload;

public class NativeQueryUser {
    private Long id;
    private String userName;
    private Long managerId;
    
    public NativeQueryUser() {
    	
    }

    public NativeQueryUser(Long id, String userName, Long managerId) {
        this.id = id;
        this.userName = userName;
        this.managerId = managerId;
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

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}



}