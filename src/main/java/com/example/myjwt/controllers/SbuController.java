package com.example.myjwt.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.models.Grade;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.ListResponse;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserListItem;
import com.example.myjwt.payload.request.CreateSbuRequest;

import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.SbuRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.services.SbuService;
import com.example.myjwt.util.PMUtils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/sbu")
public class SbuController {

	@Autowired
	private SbuRepository sbuRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private SbuService sbuService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@GetMapping("/checkSbuNameAvailability")
	public IdentityAvailability checkSbuNameAvailability(@RequestParam(value = "sbuName") String sbuName) {
		Boolean isAvailable = !sbuRepository.existsBySbuName(sbuName);
		return new IdentityAvailability(isAvailable);
	}

	@PostMapping("/createSbu")
	public ResponseEntity<?> createSbu(@Valid @RequestBody CreateSbuRequest createSbuRequest,
			HttpServletRequest request) {
		if (sbuRepository.existsBySbuName(createSbuRequest.getSbuName().trim())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: SBU name is already exist!"));
		}

		Sbu sbu = new Sbu();
		sbu.setSbuName(createSbuRequest.getSbuName());
		sbu.setIsActive(true);

		Sbu result = sbuRepository.save(sbu);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/sbu/{createSbuRequest.getSbuName()}").buildAndExpand(result.getSbuName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "SBU registered successfully!!"));
	}

	@GetMapping("/checkSBUHeadAvailability")
	public UserIdentityAvailability checkSBUHeadAvailability(
			@RequestParam(value = "sbuHeadUserName") String sbuHeadUserName) {
		List<Long> eligibleGrades = PMUtils.getSBUHeadEligibleGrades();
		Boolean isAvailable = userRepository.findByUserNameAndGradeIds(sbuHeadUserName, eligibleGrades)
				.size() > 0;
			
		return new UserIdentityAvailability(isAvailable);
	}
	
	@GetMapping("/getEligibleSBUHeads")
	public List<UserListItem> getEligibleSBUHeads() {
		return sbuService.getEligibleSbuHeads();
	}

}