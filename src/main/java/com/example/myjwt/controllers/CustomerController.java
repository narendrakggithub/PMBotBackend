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
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserProfile;
import com.example.myjwt.payload.UserSummary;
import com.example.myjwt.payload.request.CreateCustomerRequest;
import com.example.myjwt.payload.request.CreateLobRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.AccountRepository;
import com.example.myjwt.repo.CustomerRepository;
import com.example.myjwt.repo.ProjectRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.CurrentUser;
import com.example.myjwt.security.services.UserPrincipal;
import com.example.myjwt.security.services.UserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class CustomerController extends BaseController {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@GetMapping("/customer/checkCustomerNameAvailabilityForUser")
	public IdentityAvailability checkCustomerNameAvailabilityForUser(
			@RequestParam(value = "customerName") String customerName,
			@RequestParam(value = "accountId") Long accountId) {
		Boolean isAvailable = !customerRepository.existsByCustomerNameAndAccountId(customerName, accountId);
		return new IdentityAvailability(isAvailable);
	}

	@PostMapping("/customer/createCustomerRequest")
	public ResponseEntity<?> createCustomerRequest(@Valid @RequestBody CreateCustomerRequest createCustomerRequest,
			HttpServletRequest request) {
		if (customerRepository.existsByCustomerNameAndAccountId(createCustomerRequest.getCustomerName().trim(),
				createCustomerRequest.getAccountId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Customer name already exist for same account!"));
		}

		Account account = accountRepository.findByIdAndEdlId(createCustomerRequest.getAccountId(), getCurrentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Account", "accountId and userId",
						createCustomerRequest.getAccountId() + " : " + getCurrentUserId()));

		User lobLeadUser = userRepository.findByUserName(createCustomerRequest.getCustomerLeadUserName())
				.orElseThrow(() -> new ResourceNotFoundException("User", "CustomerLead", createCustomerRequest.getCustomerLeadUserName()));

		if (!userService.isUserReportingToManager(lobLeadUser.getId(), getCurrentUserId())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error: Lob lead not reporting to you!"));
		}

		System.out.println(
				"createLobRequest.getLobLeadId() -------------------------------- " + lobLeadUser.getId());

		User owner = userRepository.findById(lobLeadUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "LobLead", lobLeadUser.getId()));

		System.out.println(
				"createLobRequest.getLobName() -------------------------------- " + createCustomerRequest.getCustomerName());
		
		Customer customer = new Customer();

		customer.setCustomerName(createCustomerRequest.getCustomerName());
		customer.setAccount(account);
		customer.setIsActive(true);
		customer.setOwner(owner);

		Customer result = customerRepository.save(customer);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/{customerName}")
				.buildAndExpand(result.getCustomerName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Lob registered successfully!!"));
	}
}