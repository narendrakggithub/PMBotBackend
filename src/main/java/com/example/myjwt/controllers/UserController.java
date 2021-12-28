package com.example.myjwt.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.IdentityExists;
import com.example.myjwt.payload.NativeQueryUser;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserListItem;
import com.example.myjwt.payload.UserProfile;
import com.example.myjwt.payload.UserSummary;
import com.example.myjwt.repo.SbuRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.CurrentUser;
import com.example.myjwt.security.services.UserPrincipal;
import com.example.myjwt.security.services.UserService;
import com.example.myjwt.util.PMUtils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class UserController extends BaseController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SbuRepository sbuRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/user/me")
	public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
		UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(),
				currentUser.getRoleId());
		return userSummary;
	}

	@GetMapping("/user/checkUserNameAvailability")
	public UserIdentityAvailability checkUserNameAvailability(@RequestParam(value = "userName") String userName) {
		Boolean isAvailable = !userRepository.existsByUserName(userName);
		return new UserIdentityAvailability(isAvailable);
	}

	@GetMapping("/user/checkEmailAvailability")
	public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
		Boolean isAvailable = !userRepository.existsByEmail(email);
		return new UserIdentityAvailability(isAvailable);
	}

	@GetMapping("/user/checkManagerEmailAvailability")
	public UserIdentityAvailability checkManagerEmailAvailability(
			@RequestParam(value = "managerEmail") String managerEmail) {
		Boolean isAvailable = userRepository.existsByEmail(managerEmail);
		return new UserIdentityAvailability(isAvailable);
	}

	@GetMapping("/users/{userName}")
	public UserProfile getUserProfile(@PathVariable(value = "userName") String userName) {
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userName", userName));

		UserProfile userProfile = new UserProfile(user.getId(), user.getUserName(), user.getCreatedAt());

		return userProfile;
	}

	@GetMapping("/user/confirmSBUNameExistence")
	public IdentityExists confirmSBUNameExistence(@RequestParam(value = "sbuName") String sbuName) {
		Boolean isAvailable = sbuRepository.findBySbuHeadIdAndSbuName(getCurrentUserId(), sbuName).size()>0;
		return new IdentityExists(isAvailable);
	}

	@GetMapping("/user/confirmPDLUserExistence")
	public IdentityExists confirmPDLUserExistence(@RequestParam(value = "pdlUserName") String pdlUserName) {
		List<Long> eligibleGrades = PMUtils.getPDLEligibleGrades();
		Boolean isAvailable = userRepository.getUserWithGradeOwnedByCurrentUser(getCurrentUserId(), pdlUserName,
				eligibleGrades).size()>0;
		return new IdentityExists(isAvailable);
	}

	@GetMapping("/user/confirmEDLUserExistence")
	public IdentityExists confirmEDLUserExistence(@RequestParam(value = "edlUserName") String edlUserName) {
		List<Long> eligibleGrades = PMUtils.getEDLEligibleGrades();

		Boolean isAvailable = userRepository.getUserWithGradeOwnedByCurrentUser(getCurrentUserId(), edlUserName,
				eligibleGrades).size()>0;
		return new IdentityExists(isAvailable);
	}
	
	@GetMapping("/user/confirmLobLeadExistenceForUser")
	public IdentityExists confirmLobLeadExistenceForUser(@RequestParam(value = "lobLeadUserNameValue") String lobLeadUserNameValue) {
		System.out.println("confirmLobLeadExistenceForUser lobLeadUserNameValue ----------------------- > "+lobLeadUserNameValue+":"+getCurrentUserId());
		
		User lobLead = userRepository.findByUserName(lobLeadUserNameValue)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userName", lobLeadUserNameValue));
		
		Boolean isAvailable = userService.isUserReportingToManager(lobLead.getId(), getCurrentUserId());
		System.out.println("confirmLobLeadExistenceForUser isAvailable ----------------------- > "+isAvailable);
		return new IdentityExists(isAvailable);
	}
	
	@GetMapping("/user/confirmCustomerLeadExistenceForUser")
	public IdentityExists confirmCustomerLeadExistenceForUser(@RequestParam(value = "customerLeadUserName") String customerLeadUserName) {
		System.out.println("confirmLobLeadExistenceForUser customerLeadUserName ----------------------- > "+customerLeadUserName+":"+getCurrentUserId());
		
		User customerLead = userRepository.findByUserName(customerLeadUserName)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userName", customerLeadUserName));
		
		Boolean isAvailable = userService.isUserReportingToManager(customerLead.getId(), getCurrentUserId());
		System.out.println("confirmLobLeadExistenceForUser isAvailable ----------------------- > "+isAvailable);
		return new IdentityExists(isAvailable);
	}
	
	@GetMapping("/user/confirmSubLobLeadExistenceForUser")
	public IdentityExists confirmSubLobLeadExistenceForUser(@RequestParam(value = "subLobLeadUserName") String subLobLeadUserName) {
		System.out.println("confirmSubLobLeadExistenceForUser subLobLeadUserName ----------------------- > "+subLobLeadUserName+":"+getCurrentUserId());
		
		User subLobLead = userRepository.findByUserName(subLobLeadUserName)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userName", subLobLeadUserName));
		
		Boolean isAvailable = userService.isUserReportingToManager(subLobLead.getId(), getCurrentUserId());
		System.out.println("confirmLobLeadExistenceForUser isAvailable ----------------------- > "+isAvailable);
		return new IdentityExists(isAvailable);
	}
	
	@GetMapping("/user/getAllReporteesOfCurrentUser")
	public List<UserListItem> getAllReporteesOfCurrentUser() {
		List<UserListItem> userList = new ArrayList<UserListItem>();
		List<NativeQueryUser> allReportees = userService.getAllReporteesOf(getCurrentUserId());
		
		for (int i = 0; i < allReportees.size(); i++) {
			userList.add(new UserListItem(allReportees.get(i)));
		}
		System.out.println("getAllReporteesOfCurrentUser userList size----------------------- > "+userList.size());
		System.out.println("getAllReporteesOfCurrentUser userList ----------------------- > "+userList);

		return userList;
	}

	// select managerID, group_concat(userID) from hierarchy group by managerID ;
	@GetMapping("/user/getAllEDLUserNamesOwnedByUser")
	public List<UserListItem> getAllEDLUserNamesOwnedByUser() {
		List<Long> eligibleGrades = PMUtils.getEDLEligibleGrades();

		List<User> edlList = userRepository.findByManagerAndGradeIds(getCurrentUserId(), eligibleGrades);

		List<UserListItem> userList = new ArrayList<UserListItem>();
		for (int i = 0; i < edlList.size(); i++) {
			userList.add(new UserListItem(edlList.get(i)));
		}

		return userList;
	}

	// select managerID, group_concat(userID) from hierarchy group by managerID ;
	@GetMapping("/user/getAllPDLUserNamesOwnedByUser")
	public List<UserListItem> getAllPDLUserNamesOwnedByUser() {
		List<Long> eligibleGrades = PMUtils.getPDLEligibleGrades();

		List<User> edlList = userRepository.findByManagerAndGradeIds(getCurrentUserId(), eligibleGrades);

		List<UserListItem> userList = new ArrayList<UserListItem>();
		for (int i = 0; i < edlList.size(); i++) {
			userList.add(new UserListItem(edlList.get(i)));
		}

		return userList;
	}

}