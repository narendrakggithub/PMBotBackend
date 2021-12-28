package com.example.myjwt.controllers;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.Account;
import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Lob;
import com.example.myjwt.models.SubLob;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserProfile;
import com.example.myjwt.payload.UserSummary;
import com.example.myjwt.payload.request.CreateCustomerRequest;
import com.example.myjwt.payload.request.CreateSubLobRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.LobRepository;
import com.example.myjwt.repo.ProjectRepository;
import com.example.myjwt.repo.SubLobRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.CurrentUser;
import com.example.myjwt.security.services.UserPrincipal;
import com.example.myjwt.security.services.UserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class SubLobController extends BaseController {

	@Autowired
	private SubLobRepository subLobRepository;
	
	@Autowired
	private LobRepository lobRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@GetMapping("/subLob/checkSubLobNameAvailabilityForUser")
	public IdentityAvailability checkSubLobNameAvailabilityForUser(
			@RequestParam(value = "subLobName") String subLobName,
			@RequestParam(value = "lobId") Long lobId) {
		Boolean isAvailable = !subLobRepository.existsBySubLobNameAndLobId(subLobName, lobId);
		return new IdentityAvailability(isAvailable);
	}
	
	@PostMapping("/subLob/createSubLobRequest")
	public ResponseEntity<?> createSubLobRequest(@Valid @RequestBody CreateSubLobRequest createSubLobRequest,
			HttpServletRequest request) {
		if (subLobRepository.existsBySubLobNameAndLobId(createSubLobRequest.getSubLobName().trim(),
				createSubLobRequest.getLobId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: SubLob name already exist for same Lob!"));
		}

		Lob lob = lobRepository.findByIdAndOwnerId(createSubLobRequest.getLobId(), getCurrentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Lob", "lobId and userId",
						createSubLobRequest.getLobId() + " : " + getCurrentUserId()));

		User subLobLeadUser = userRepository.findByUserName(createSubLobRequest.getSubLobLeadUserName())
				.orElseThrow(() -> new ResourceNotFoundException("User", "SubLobLead", createSubLobRequest.getSubLobLeadUserName()));

		if (!userService.isUserReportingToManager(subLobLeadUser.getId(), getCurrentUserId())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: SubLob lead not reporting to you!"));
		}

		System.out.println(
				"subLobLeadUser.getId() -------------------------------- " + subLobLeadUser.getId());

		User owner = userRepository.findById(subLobLeadUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "SubLobLead", subLobLeadUser.getId()));

		System.out.println(
				"createSubLobRequest.getSubLobName() -------------------------------- " + createSubLobRequest.getSubLobName());
		
		SubLob subLob = new SubLob();
		subLob.setSubLobName(createSubLobRequest.getSubLobName());
		subLob.setIsActive(true);
		subLob.setLob(lob);
		subLob.setOwner(owner);

		SubLob result = subLobRepository.save(subLob);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sublob/{createSubLobRequest.getSubLobName()}")
				.buildAndExpand(result.getSubLobName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "SubLob registered successfully!!"));
	}

}