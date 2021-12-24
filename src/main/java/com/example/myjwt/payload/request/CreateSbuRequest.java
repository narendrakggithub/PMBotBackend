package com.example.myjwt.payload.request;

import java.util.Set;

import javax.validation.constraints.*;
 
public class CreateSbuRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String sbuName;

	public String getSbuName() {
		return sbuName;
	}

	public void setSbuName(String sbuName) {
		this.sbuName = sbuName;
	}
 
   
    
}
