package com.example.myjwt.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserProfile;
import com.example.myjwt.payload.UserSummary;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.CurrentUser;
import com.example.myjwt.security.services.UserPrincipal;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/user/me")
	public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
		UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getRoleId());
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
	public UserIdentityAvailability checkManagerEmailAvailability(@RequestParam(value = "managerEmail") String managerEmail) {
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
	
	

}