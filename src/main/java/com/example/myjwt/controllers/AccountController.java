package com.example.myjwt.controllers;

import java.net.URI;
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
import com.example.myjwt.models.Grade;
import com.example.myjwt.models.Role;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.request.CreateAccountRequest;
import com.example.myjwt.payload.request.CreateSbuRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.AccountRepository;
import com.example.myjwt.repo.SbuRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.services.RoleService;
import com.example.myjwt.util.PMUtils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class AccountController extends BaseController {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SbuRepository sbuRepository;

	@Autowired
	private RoleService roleService;

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	@GetMapping("/account/checkAccountNameAvailability")
	public IdentityAvailability checkAccountNameAvailability(@RequestParam(value = "accountName") String accountName) {
		Boolean isAvailable = !accountRepository.existsByAccountName(accountName);
		return new IdentityAvailability(isAvailable);
	}

	@GetMapping("/account/confirmAccountIdExistenceForUser")
	public IdentityAvailability confirmAccountIdExistenceForUser(@RequestParam(value = "accountId") Long accountId) {

		Long currentUserId = getCurrentUserId();

		Boolean isAvailable = accountRepository.existsByIdAndEdlId(accountId, currentUserId);

		System.out.println("confirmAccountIdExistenceForUser isAvailable ----------------------- > " + isAvailable);

		return new IdentityAvailability(isAvailable);
	}

	@GetMapping("/account/getAllAccountsOwnedByUser")
	public List<Account> getAllAccountsOwnedByUser() {

		Long currentUserId = getCurrentUserId();

		List<Account> accounts = accountRepository.findByEdlId(currentUserId);

		System.out.println("accounts  ----------------------- > " + accounts);

		return accounts;
	}

	@GetMapping("/account/getAccountsToAssignEDL")
	public List<Account> getAccountsToAssignEDL() {

		Long currentUserId = getCurrentUserId();

		List<Account> accounts = accountRepository.findByPdlIdAndEdlIdAndIsActive(currentUserId, null, true);

		System.out.println("accounts  ----------------------- > " + accounts);

		return accounts;
	}

	@PreAuthorize("hasAuthority('SBUHead')")
	@PostMapping("/account/createAccount")
	public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest,
			HttpServletRequest request) {
		if (accountRepository.existsByAccountName(createAccountRequest.getAccountName().trim())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: Account name is already exist!"));
		}

		System.out.println("createSbuRequest.getSbuName() -------------------------------- "
				+ createAccountRequest.getAccountName());
		Account account = new Account();
		account.setAccountName(createAccountRequest.getAccountName());

		String sbuName = createAccountRequest.getSbuName();
		List<Sbu> sbuList = sbuRepository.findBySbuHeadIdAndSbuName(getCurrentUserId(), sbuName);
		if (sbuList.size() != 1) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: There are too many or zero SBU with this name under you!"));
		}
		System.out.println("sbuList.get(0) -----------------------> " + sbuList.get(0).getId());
		account.setSbu(sbuList.get(0));

		String pdlUserName = createAccountRequest.getPdlUserName();
		List<Long> eligibleGrades = PMUtils.getPDLEligibleGrades();

		List<User> pdlUsers = userRepository.getUserWithGradeOwnedByCurrentUser(getCurrentUserId(), pdlUserName,
				eligibleGrades);

		if (pdlUsers.size() != 1) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: There are too many or zero PDLs with same name under you!"));
		}

		User pdl = pdlUsers.get(0);

		account.setPdl(pdl);

		Set<Role> roles = roleService.getAllRolesFor(ERole.PDL);
		pdl.setRoles(roles);

		account.setIsActive(true);

		Account result = createAccountTransaction(pdl, account);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sbu/{accountName}")
				.buildAndExpand(result.getAccountName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Account registered successfully!!"));
	}

	@Transactional
	private Account createAccountTransaction(User user, Account account) {
		userRepository.save(user);
		Account result = accountRepository.save(account);
		return result;
	}

	@GetMapping("/account/assignEDLForAccount")
	public ResponseEntity<?> assignEDLForAccount(@RequestParam(value = "edlUserName") String edlUserName,
			@RequestParam(value = "accountName") String accountName) {

		System.out.println("accountName,edlUserName --------------------> " + accountName + ", " + edlUserName);

		User edlUser = userRepository.findByUserName(edlUserName)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userName", edlUserName));

		Long currentUserId = getCurrentUserId();
		if (!edlUser.getManager().getId().equals(currentUserId)) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: You are not the reporting manager for this associate!"));
		}

		Account account = accountRepository.findByAccountNameAndPdlIdAndIsActive(accountName, currentUserId, true);

		if (account == null) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: Account doesn't exists"));
		}

		account.setEdl(edlUser);
		
		Set<Role> roles = roleService.getAllRolesFor(ERole.EDL);
		edlUser.setRoles(roles);
		
		Account result = createAccountTransaction(edlUser, account);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(result.getAccountName()).toUri();

		System.out.println("result.getAccountName() --------------------> " + result.getAccountName());

		return ResponseEntity.created(location).body(new ApiResponse(true, "EDL assigned"));
	}
	
}