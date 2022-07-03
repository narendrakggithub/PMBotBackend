package com.example.myjwt.controllers;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.Account;
import com.example.myjwt.models.Lob;
import com.example.myjwt.models.Role;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.request.CreateLobRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.AccountRepository;
import com.example.myjwt.repo.LobRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.services.RoleService;
import com.example.myjwt.security.services.UserService;

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
	
	@Autowired
	private RoleService roleService;

	private static final Logger logger = LoggerFactory.getLogger(LobController.class);

	@GetMapping("/lob/checkLobNameAvailabilityForUser")
	public IdentityAvailability checkLobNameAvailabilityForUser(@RequestParam(value = "lobName") String lobName,
			@RequestParam(value = "accountId") Long accountId) {

		System.out.println("checkLobNameAvailabilityForUser accountId ----------------------- > " + accountId);

		Boolean isAvailable = !lobRepository.existsByLobNameAndAccountId(lobName, accountId);

		System.out.println("checkLobNameAvailabilityForUser isAvailable ----------------------- > " + isAvailable);

		return new IdentityAvailability(isAvailable);
	}

	@GetMapping("/lob/confirmLobIdExistenceForUser")
	public IdentityAvailability confirmLobIdExistenceForUser(@RequestParam(value = "lobId") Long lobId) {

		Long currentUserId = getCurrentUserId();

		Boolean isAvailable = lobRepository.existsByIdAndOwnerId(lobId, currentUserId);

		System.out.println("confirmLobIdExistenceForUser isAvailable ----------------------- > " + isAvailable);

		return new IdentityAvailability(isAvailable);
	}

	@GetMapping("/lob/getAllLobsOwnedByUser")
	public List<Lob> getAllLobsOwnedByUser() {

		Long currentUserId = getCurrentUserId();

		List<Lob> lobs = lobRepository.findByOwnerId(currentUserId);

		System.out.println("lobs  ----------------------- > " + lobs);

		return lobs;
	}

	@PreAuthorize("hasAuthority('EDL')")
	@PostMapping("/lob/createLobRequest")
	public ResponseEntity<?> createLobRequest(@Valid @RequestBody CreateLobRequest createLobRequest,
			HttpServletRequest request) {

		String lobName = createLobRequest.getLobName().trim();

		if (lobRepository.existsByLobNameAndAccountId(lobName, createLobRequest.getAccountId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Lob name already exist for same account!"));
		}

		Account account = accountRepository.findByIdAndEdlId(createLobRequest.getAccountId(), getCurrentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Account", "accountId and userId",
						createLobRequest.getAccountId() + " : " + getCurrentUserId()));

		User lobLeadUser = userRepository.findByUserName(createLobRequest.getLobLeadUserName()).orElseThrow(
				() -> new ResourceNotFoundException("User", "LobLead", createLobRequest.getLobLeadUserName()));

		if (!userService.isUserReportingToManager(lobLeadUser.getId(), getCurrentUserId())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: Lob lead not reporting to you!"));
		}

		System.out.println("createLobRequest.getLobLeadId() -------------------------------- " + lobLeadUser.getId());
		System.out.println("createLobRequest.getLobName() -------------------------------- " + lobName);
		
		Lob lob = new Lob();
		lob.setLobName(lobName);
		lob.setAccount(account);
		lob.setIsActive(true);
		lob.setOwner(lobLeadUser);
		
		Set<Role> roles = roleService.getAllRolesFor(ERole.LOBLead);
		lobLeadUser.setRoles(roles);

		Lob result = createLobTransaction(lobLeadUser, lob);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/lob/{lobName}")
				.buildAndExpand(result.getLobName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Lob registered successfully!!"));
	}

	@Transactional
	private Lob createLobTransaction(User user, Lob lob) {
		userRepository.save(user);
		Lob result = lobRepository.save(lob);
		return result;
	}

}