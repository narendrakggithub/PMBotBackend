package com.example.myjwt.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.ListResponse;
import com.example.myjwt.payload.NativeQueryUser;
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
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SbuRepository sbuRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	public List<NativeQueryUser> getAllReporteesOf(Long managerId) {
		List<NativeQueryUser> allReportees = new ArrayList<NativeQueryUser>();
		List<Object[]> reportees = userRepository.getAllUserUnderManagerWithId(managerId);

		for (Object[] a : reportees) {
			allReportees.add(reporteeArrayToNativeQueryUser(a));
		}

		return allReportees;
	}
	
	public boolean isUserReportingToManager(Long userId, Long managerId) {
		List<NativeQueryUser> allReportees = getAllReporteesOf(managerId);
		Boolean isAvailable = false;
		for(NativeQueryUser nativeQueryUser: allReportees) {
			System.out.println("user.getId().longValue() == managerId.longValue(): "+nativeQueryUser.getId().longValue()+":"+managerId.longValue());
			if(nativeQueryUser.getManagerId().longValue() == managerId.longValue()) {
				isAvailable = true;
				break;
			}
		}
		return isAvailable;
	}

	public NativeQueryUser reporteeArrayToNativeQueryUser(Object[] array) {
		NativeQueryUser nativeQueryUser = new NativeQueryUser();
		nativeQueryUser.setId(Long.parseLong(array[0].toString()));
		nativeQueryUser.setUserName(array[1].toString());
		nativeQueryUser.setManagerId(Long.parseLong(array[2].toString()));
		return nativeQueryUser;
	}
}
