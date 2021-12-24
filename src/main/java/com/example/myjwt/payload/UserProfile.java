package com.example.myjwt.payload;

import java.time.Instant;

public class UserProfile {
	private Long id;
    private String userName;
    private Instant joinedAt;

    public UserProfile(Long id, String userName, Instant joinedAt) {
        this.id = id;
        this.userName = userName;
        this.joinedAt = joinedAt;
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

	public Instant getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(Instant joinedAt) {
		this.joinedAt = joinedAt;
	}

}
