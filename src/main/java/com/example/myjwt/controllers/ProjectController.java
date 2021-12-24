package com.example.myjwt.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserProfile;
import com.example.myjwt.payload.UserSummary;
import com.example.myjwt.repo.ProjectRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.CurrentUser;
import com.example.myjwt.security.services.UserPrincipal;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ProjectController {

	@Autowired
	private ProjectRepository projectRepository;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);


	@GetMapping("/project/checkProjectNameAvailability")
	public IdentityAvailability checkProjectNameAvailability(@RequestParam(value = "projectName") String projectName) {
		Boolean isAvailable = !projectRepository.existsByProjectName(projectName);
		return new IdentityAvailability(isAvailable);
	}

}