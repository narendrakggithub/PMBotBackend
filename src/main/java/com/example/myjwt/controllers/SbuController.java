package com.example.myjwt.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.models.Grade;
import com.example.myjwt.models.Hexcode;
import com.example.myjwt.models.Role;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.ListResponse;
import com.example.myjwt.payload.NativeQueryUser;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserListItem;
import com.example.myjwt.payload.request.CreateSbuRequest;

import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.SbuRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.services.RoleService;
import com.example.myjwt.security.services.SbuService;
import com.example.myjwt.security.services.UserPrincipal;
import com.example.myjwt.security.services.UserService;
import com.example.myjwt.util.PMUtils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/sbu")
public class SbuController extends BaseController {

	@Autowired
	private SbuRepository sbuRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SbuService sbuService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@GetMapping("/checkSbuNameAvailability")
	public IdentityAvailability checkSbuNameAvailability(@RequestParam(value = "sbuName") String sbuName) {
		Boolean isAvailable = !sbuRepository.existsBySbuName(sbuName);
		return new IdentityAvailability(isAvailable);
	}

	@PreAuthorize("hasAuthority('Admin')")
	@PostMapping("/createSbu")
	public ResponseEntity<?> createSbu(@Valid @RequestBody CreateSbuRequest createSbuRequest,
			HttpServletRequest request) {
		String strSbuName = createSbuRequest.getSbuName().trim();

		if (sbuRepository.existsBySbuName(strSbuName)) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: SBU name is already exist!"));
		}

		Sbu sbu = new Sbu();
		sbu.setSbuName(strSbuName);
		sbu.setIsActive(true);

		List<Long> eligibleGrades = PMUtils.getSBUHeadEligibleGrades();
		Boolean isAvailable = userRepository
				.findByUserNameAndGradeIds(createSbuRequest.getSbuHeadUserName(), eligibleGrades).size() > 0;
				
				System.out.println("isAvailableisAvailable="+isAvailable);

		Sbu result = null;
		if (isAvailable) {
			User sbuHead = userRepository.findByUserName(createSbuRequest.getSbuHeadUserName())
					.orElseThrow(() -> new UsernameNotFoundException(
							"SBU head not Found with userName: " + createSbuRequest.getSbuHeadUserName()));

			Set<Role> roles = roleService.getAllRolesFor(ERole.SBUHead);
			sbuHead.setRoles(roles);

			sbu.setSbuHead(sbuHead);
			result = createSBUTransaction(sbuHead, sbu);
			
			URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/api/sbu/{createSbuRequest.getSbuName()}").buildAndExpand(result.getSbuName()).toUri();

			return ResponseEntity.created(location).body(new ApiResponse(true, "SBU registered successfully!!"));

		} else {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "SBU head selected is not eligible for this role!"));
		}	
	}

	@Transactional
	private Sbu createSBUTransaction(User user, Sbu sbu) {
		userRepository.save(user);
		Sbu result = sbuRepository.save(sbu);
		return result;
	}

	@GetMapping("/checkSBUHeadAvailability")
	public UserIdentityAvailability checkSBUHeadAvailability(
			@RequestParam(value = "sbuHeadUserName") String sbuHeadUserName) {

		System.out.println("sbuHeadUserName = " + sbuHeadUserName);
		List<Long> eligibleGrades = PMUtils.getSBUHeadEligibleGrades();
		Boolean isAvailable = userRepository.findByUserNameAndGradeIds(sbuHeadUserName, eligibleGrades).size() > 0;

		System.out.println("isAvailable:" + isAvailable);
		return new UserIdentityAvailability(isAvailable);
	}

	@GetMapping("/getEligibleSBUHeads")
	public List<UserListItem> getEligibleSBUHeads() {

		List<NativeQueryUser> allReportees = userService.getAllReporteesOf(getCurrentUserId());

		List<Long> eligibleGrades = PMUtils.getSBUHeadEligibleGrades();
		List<UserListItem> eligibleSbuHeads = new ArrayList<UserListItem>();

		for (NativeQueryUser nativeQueryUser : allReportees) {
			if (eligibleGrades.contains(nativeQueryUser.getGradeId())) {
				UserListItem listItem = new UserListItem(nativeQueryUser);
				eligibleSbuHeads.add(listItem);
			}
		}

		return eligibleSbuHeads;
	}

	@GetMapping("/getAllSBUNamesOwnedByUser")
	public List<Sbu> getAllSBUNamesOwnedByUser() {
		return sbuService.getAllSbuOwnedByUser(getCurrentUserId());
	}

}