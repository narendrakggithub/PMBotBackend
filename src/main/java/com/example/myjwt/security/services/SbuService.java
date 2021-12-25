package com.example.myjwt.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myjwt.models.User;
import com.example.myjwt.payload.ListResponse;
import com.example.myjwt.payload.UserListItem;
import com.example.myjwt.repo.SbuRepository;
import com.example.myjwt.repo.SubLobRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.util.PMUtils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SbuService {

	@Autowired
	private UserRepository userRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(SbuService.class);
	
	public List<UserListItem> getEligibleSbuHeads() {
		List<Long> eligibleGrades = PMUtils.getSBUHeadEligibleGrades();
		
		List<User> listUsers = userRepository.findEligibleSBUHeads(eligibleGrades);
		List<UserListItem> eligibleSbuHeads = new ArrayList<UserListItem>();
		for(int i=0; i<listUsers.size(); i++) {
			UserListItem listItem = new UserListItem();
			User user = listUsers.get(i);
			listItem.setId(user.getId());
			listItem.setUserName(user.getUserName());
			listItem.setUserFullName(user.getFullName());
			listItem.setGrade(user.getGrade().getName().name());
			eligibleSbuHeads.add(listItem);
		}
			
        return eligibleSbuHeads;
    }
}
