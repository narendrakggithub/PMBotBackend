package com.example.myjwt.controllers;

import java.net.URI;

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
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.request.CreateAccountRequest;
import com.example.myjwt.payload.request.CreateSbuRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.AccountRepository;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class AccountController {

	@Autowired
	private AccountRepository accountRepository;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);


	@GetMapping("/account/checkAccountNameAvailability")
	public IdentityAvailability checkAccountNameAvailability(@RequestParam(value = "accountName") String accountName) {
		Boolean isAvailable = !accountRepository.existsByAccountName(accountName);
		return new IdentityAvailability(isAvailable);
	}
	
	@PostMapping("/createAccount")
	public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest,
			HttpServletRequest request) {
		if (accountRepository.existsByAccountName(createAccountRequest.getAccountName().trim())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: SBU name is already exist!"));
		}

		System.out.println("createSbuRequest.getSbuName() -------------------------------- "+createAccountRequest.getAccountName());
		Account account = new Account();
		account.setAccountName(createAccountRequest.getAccountName());
		account.setIsActive(true);

		Account result = accountRepository.save(account);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/sbu/{accountName}").buildAndExpand(result.getAccountName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Account registered successfully!!"));
	}

}