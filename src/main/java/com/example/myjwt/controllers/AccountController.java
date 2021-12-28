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

import com.example.myjwt.models.Account;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.request.CreateAccountRequest;
import com.example.myjwt.payload.request.CreateSbuRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.AccountRepository;
import com.example.myjwt.repo.SbuRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.util.PMUtils;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class AccountController extends BaseController{

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SbuRepository sbuRepository;

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
		
		System.out.println("confirmAccountIdExistenceForUser isAvailable ----------------------- > "+isAvailable);
		
		return new IdentityAvailability(isAvailable);
	}
	
	@GetMapping("/account/getAllAccountsOwnedByUser")
	public List<Account> getAllAccountsOwnedByUser() {
		
		Long currentUserId = getCurrentUserId();
		
		List<Account> accounts = accountRepository.findByEdlId(currentUserId);
		
		System.out.println("accounts  ----------------------- > "+accounts);
		
		return accounts;
	}
	
	@PostMapping("/account/createAccount")
	public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest,
			HttpServletRequest request) {
		if (accountRepository.existsByAccountName(createAccountRequest.getAccountName().trim())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: Account name is already exist!"));
		}

		System.out.println("createSbuRequest.getSbuName() -------------------------------- "+createAccountRequest.getAccountName());
		Account account = new Account();
		account.setAccountName(createAccountRequest.getAccountName());
		
		String sbuName = createAccountRequest.getSbuName();
		List<Sbu> sbuList = sbuRepository.findBySbuHeadIdAndSbuName(getCurrentUserId(), sbuName);
		if(sbuList.size()!=1) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: There are too many or zero SBU with this name under you!"));
		}
		System.out.println("sbuList.get(0) -----------------------> "+sbuList.get(0).getId());
		account.setSbu(sbuList.get(0));
		
		String pdlUserName = createAccountRequest.getPdlUserName();
		List<Long> eligibleGrades = PMUtils.getPDLEligibleGrades();
		
		List<User> pdlUsers = userRepository.getUserWithGradeOwnedByCurrentUser(getCurrentUserId(), pdlUserName,
				eligibleGrades);
		
		if(pdlUsers.size()!=1) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: There are too many or zero PDLs with same name under you!"));
		}
		
		account.setPdl(pdlUsers.get(0));
		account.setIsActive(true);

		Account result = accountRepository.save(account);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/sbu/{accountName}").buildAndExpand(result.getAccountName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Account registered successfully!!"));
	}

}