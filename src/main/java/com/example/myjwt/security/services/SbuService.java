package com.example.myjwt.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.example.myjwt.models.Sbu;
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
	
	@Autowired
	private SbuRepository sbuRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(SbuService.class);
	
	public List<UserListItem> getEligibleSbuHeads() {
		List<Long> eligibleGrades = PMUtils.getSBUHeadEligibleGrades();
		
		List<User> listUsers = userRepository.findEligibleSBUHeads(eligibleGrades);
		List<UserListItem> eligibleSbuHeads = new ArrayList<UserListItem>();
		for(int i=0; i<listUsers.size(); i++) {
			UserListItem listItem = new UserListItem(listUsers.get(i));
			eligibleSbuHeads.add(listItem);
		}
			
        return eligibleSbuHeads;
    }
	
	public List<Sbu> getAllSbuOwnedByUser(Long sbuHeadId) {
		
		System.out.println("getAllSbuOwnedByUser ----------------->"+sbuHeadId);
		
		List<Sbu> list = sbuRepository.findBySbuHeadId(sbuHeadId);
		
		for(int i=0; i<list.size(); i++) {
			Sbu sbu = list.get(i);
			System.out.println("sbu.getId() ----------------->"+sbu.getId());
			System.out.println("sbu.getSbuName() ----------------->"+sbu.getSbuName());
			System.out.println("sbu.getSbuHead().getUserName() ----------------->"+sbu.getSbuHead().getUserName());
			System.out.println("sbu.getSbuHead().getManager().getUserName() ----------------->"+sbu.getSbuHead().getManager().getUserName());		
			System.out.println("sbu.getSbuHead().getManager().getGrade().getName().name() ----------------->"+sbu.getSbuHead().getManager().getGrade().getName().name());		
		}
		
		System.out.println("getAllSbuOwnedByUser ----------------->"+list);
		
		return list;
	}
}
