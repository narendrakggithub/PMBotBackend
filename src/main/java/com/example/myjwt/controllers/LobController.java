package com.example.myjwt.controllers;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.Account;
import com.example.myjwt.models.Lob;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.request.CreateAccountRequest;
import com.example.myjwt.payload.request.CreateLobRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.AccountRepository;
import com.example.myjwt.repo.LobRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.services.UserService;
import com.example.myjwt.util.PMUtils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class LobController extends BaseController {

	@Autowired
	private LobRepository lobRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@GetMapping("/lob/checkLobNameAvailabilityForUser")
	public IdentityAvailability checkLobNameAvailabilityForUser(@RequestParam(value = "lobName") String lobName,
			@RequestParam(value = "accountId") Long accountId) {

		System.out.println("checkLobNameAvailabilityForUser accountId ----------------------- > " + accountId);

		Long userId = getCurrentUserId();

		Boolean isAvailable = !lobRepository.existsByLobNameAndAccountId(lobName, accountId);

		System.out.println("checkLobNameAvailabilityForUser isAvailable ----------------------- > " + isAvailable);

		return new IdentityAvailability(isAvailable);
	}

	@PostMapping("/lob/createLobRequest")
	public ResponseEntity<?> createLobRequest(@Valid @RequestBody CreateLobRequest createLobRequest,
			HttpServletRequest request) {
		if (lobRepository.existsByLobNameAndAccountId(createLobRequest.getLobName().trim(),
				createLobRequest.getAccountId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Lob name already exist for same account!"));
		}

		Account account = accountRepository.findByIdAndEdlId(createLobRequest.getAccountId(), getCurrentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Account", "accountId and userId",
						createLobRequest.getAccountId() + " : " + getCurrentUserId()));

		User lobLeadUser = userRepository.findByUserName(createLobRequest.getLobLeadUserName())
				.orElseThrow(() -> new ResourceNotFoundException("User", "LobLead", createLobRequest.getLobLeadUserName()));

		if (!userService.isUserReportingToManager(lobLeadUser.getId(), getCurrentUserId())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: Lob lead not reporting to you!"));
		}

		System.out.println(
				"createLobRequest.getLobLeadId() -------------------------------- " + lobLeadUser.getId());

		User owner = userRepository.findById(lobLeadUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "LobLead", lobLeadUser.getId()));

		System.out.println(
				"createLobRequest.getLobName() -------------------------------- " + createLobRequest.getLobName());
		Lob lob = new Lob();
		lob.setLobName(createLobRequest.getLobName());
		lob.setAccount(account);
		lob.setIsActive(true);
		lob.setOwner(owner);

		Lob result = lobRepository.save(lob);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/lob/{lobName}")
				.buildAndExpand(result.getLobName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Lob registered successfully!!"));
	}

}