package com.example.myjwt.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.Account;
import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Lob;
import com.example.myjwt.models.Role;
import com.example.myjwt.models.SubLob;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
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
import com.example.myjwt.security.services.RoleService;
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

	@Autowired
	private RoleService roleService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@GetMapping("/subLob/checkSubLobNameAvailabilityForUser")
	public IdentityAvailability checkSubLobNameAvailabilityForUser(
			@RequestParam(value = "subLobName") String subLobName, @RequestParam(value = "lobId") Long lobId) {
		Boolean isAvailable = !subLobRepository.existsBySubLobNameAndLobId(subLobName, lobId);
		return new IdentityAvailability(isAvailable);
	}

	@PostMapping("/subLob/createSubLobRequest")
	public ResponseEntity<?> createSubLobRequest(@Valid @RequestBody CreateSubLobRequest createSubLobRequest,
			HttpServletRequest request) {
		String subLobName = createSubLobRequest.getSubLobName().trim();
		String subLobUserName = createSubLobRequest.getSubLobLeadUserName().trim();
		if (subLobRepository.existsBySubLobNameAndLobId(subLobName, createSubLobRequest.getLobId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: SubLob name already exist for same Lob!"));
		}

		Lob lob = lobRepository.findByIdAndOwnerId(createSubLobRequest.getLobId(), getCurrentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Lob", "lobId and userId",
						createSubLobRequest.getLobId() + " : " + getCurrentUserId()));

		User subLobLeadUser = userRepository.findByUserName(subLobUserName)
				.orElseThrow(() -> new ResourceNotFoundException("User", "SubLobLead", subLobUserName));

		if (!userService.isUserReportingToManager(subLobLeadUser.getId(), getCurrentUserId())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: SubLob lead not reporting to you!"));
		}

		System.out.println("subLobLeadUser.getId() -------------------------------- " + subLobLeadUser.getId());

		System.out.println("createSubLobRequest.getSubLobName() -------------------------------- " + subLobName);

		Set<Role> roles = roleService.getAllRolesFor(ERole.SubLOBLead);
		subLobLeadUser.setRoles(roles);

		SubLob subLob = new SubLob();
		subLob.setSubLobName(subLobName);
		subLob.setIsActive(true);
		subLob.setLob(lob);
		subLob.setOwner(subLobLeadUser);

		SubLob result = createSubLobTransaction(subLobLeadUser, subLob);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/sublob/{createSubLobRequest.getSubLobName()}").buildAndExpand(result.getSubLobName())
				.toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "SubLob registered successfully!!"));
	}

	@Transactional
	private SubLob createSubLobTransaction(User user, SubLob subLob) {
		userRepository.save(user);
		SubLob result = subLobRepository.save(subLob);
		return result;
	}

	@GetMapping("/sublob/getMySubLobList")
	public List<SubLob> getMySubLobList() {
		Long currentUserId = getCurrentUserId();

		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user", currentUserId));

		Set<Role> roles = user.getRoles();

		boolean isSubLobLead = false;

		for (Role role : roles) {
			if (role.getName().equals(ERole.SubLOBLead)) {
				isSubLobLead = true;
				break;
			}
		}
		List<SubLob> subLobs = null;
		if (isSubLobLead)
			subLobs = subLobRepository.findByOwnerId(currentUserId);
		else {
			List<Object[]> subLobsArray = subLobRepository
					.findAllSubLobsInSameAccountOfLoggedInCustomerHead(currentUserId);
			subLobs = new ArrayList<SubLob>();
			for (Object[] arr : subLobsArray) {
				subLobs.add(nativeQueryToSubLob(arr));
			}
		}
		System.out.println("subLobs.size  ----------------------- > " + subLobs.size());
		return subLobs;
	}

	public SubLob nativeQueryToSubLob(Object[] arrValues) {
		SubLob subLob = new SubLob();
		int i = 0;
		subLob.setId(Long.parseLong(arrValues[i++].toString()));
		subLob.setSubLobName(arrValues[i++].toString());
		return subLob;
	}

	@GetMapping("/subLob/checkSubLobIDAvailabilityForUser")
	public IdentityAvailability checkSubLobIDAvailabilityForUser(
			@RequestParam(value = "subLobIdValue") Long subLobIdValue) {
		Long currentUserId = getCurrentUserId();

		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user", currentUserId));

		Set<Role> roles = user.getRoles();

		boolean isSubLobLead = false;

		for (Role role : roles) {
			if (role.getName().equals(ERole.SubLOBLead)) {
				isSubLobLead = true;
				break;
			}
		}

		boolean isOK = false;

		List<SubLob> subLobList = null;
		if (isSubLobLead) {
			SubLob subLob = subLobRepository.findByIdAndOwnerId(subLobIdValue, currentUserId);
			isOK = subLob != null;
		} else {
			List<Object[]> subLobsArray = subLobRepository
					.findAllSubLobsInSameAccountOfLoggedInCustomerHead(currentUserId);
			
			subLobList = new ArrayList<SubLob>();
			for (Object[] arr : subLobsArray) {
				subLobList.add(nativeQueryToSubLob(arr));
			}

			for (SubLob subLob : subLobList) {
				if (subLob.getId() == subLobIdValue) {
					isOK = true;
					break;
				}
			}
		}

		return new IdentityAvailability(isOK);
	}

}