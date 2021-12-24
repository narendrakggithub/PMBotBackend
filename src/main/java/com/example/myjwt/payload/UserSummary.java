package com.example.myjwt.payload;

public class UserSummary {
    private Long id;
    private String userName;
    private Long roleId;

    public UserSummary(Long id, String userName, Long roleId) {
        this.id = id;
        this.userName = userName;
        this.roleId = roleId;
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

}