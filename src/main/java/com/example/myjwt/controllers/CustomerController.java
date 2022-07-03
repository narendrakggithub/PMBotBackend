package com.example.myjwt.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.Account;
import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Role;
import com.example.myjwt.models.SubLob;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.NativeQueryUser;
import com.example.myjwt.payload.request.CreateCustomerRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.AccountRepository;
import com.example.myjwt.repo.CustomerRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.services.RoleService;
import com.example.myjwt.security.services.UserService;
import com.example.myjwt.util.PMUtils;

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

	@Autowired
	private RoleService roleService;

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

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

		String cusotomerName = createCustomerRequest.getCustomerName().trim();
		String customerLeadUserName = createCustomerRequest.getCustomerLeadUserName().trim();

		if (customerRepository.existsByCustomerNameAndAccountId(cusotomerName, createCustomerRequest.getAccountId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Customer name already exist for same account!"));
		}

		Account account = accountRepository.findByIdAndEdlId(createCustomerRequest.getAccountId(), getCurrentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Account", "accountId and userId",
						createCustomerRequest.getAccountId() + " : " + getCurrentUserId()));

		User customerLeadUser = userRepository.findByUserName(customerLeadUserName)
				.orElseThrow(() -> new ResourceNotFoundException("User", "CustomerLead", customerLeadUserName));

		if (!userService.isUserReportingToManager(customerLeadUser.getId(), getCurrentUserId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Customer lead not reporting to you!"));
		}

		System.out.println("customerLeadUser.getId() -------------------------------- " + customerLeadUser.getId());

		System.out.println("cusotomerName -------------------------------- " + cusotomerName);

		Customer customer = new Customer();

		Set<Role> roles = roleService.getAllRolesFor(ERole.CustomerLead);
		customerLeadUser.setRoles(roles);

		customer.setCustomerName(cusotomerName);
		customer.setAccount(account);
		customer.setIsActive(true);
		customer.setOwner(customerLeadUser);

		Customer result = createCustomerTransaction(customerLeadUser, customer);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer/{customerName}")
				.buildAndExpand(result.getCustomerName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Customer registered successfully!!"));
	}

	@Transactional
	private Customer createCustomerTransaction(User user, Customer customer) {
		userRepository.save(user);
		Customer result = customerRepository.save(customer);
		return result;
	}

	@GetMapping("/customer/getMyCustomerList")
	public List<Customer> getMyCustomerList() {
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

		List<Customer> customerList = null;
		if (isSubLobLead) {
			List<Object[]> customers = customerRepository.findCustomerInSameAccountOfLoggedInSubLobHead(currentUserId);
			customerList = new ArrayList<Customer>();
			for (Object[] arr : customers) {
				customerList.add(nativeQueryToCustomer(arr));
			}

			
		} else {
			customerList = customerRepository.findByOwnerId(currentUserId);
		}

		System.out.println("customerList.size  ----------------------- > " + customerList.size());
		return customerList;
	}
	
	public Customer nativeQueryToCustomer(Object[] arrValues) {

		Customer customer = new Customer();
		int i=0;
		customer.setId(Long.parseLong(arrValues[i++].toString()));
		customer.setCustomerName(arrValues[i++].toString());
		return customer;
		
	}

	@GetMapping("/customer/checkCustomerIdAvailabilityForUser")
	public IdentityAvailability checkCustomerIdAvailabilityForUser(
			@RequestParam(value = "customerIdValue") Long customerIdValue) {
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
		List<Customer> customerList = null;
		if (isSubLobLead) {
			List<Object[]> customers = customerRepository.findCustomerInSameAccountOfLoggedInSubLobHead(currentUserId);
			customerList = new ArrayList<Customer>();
			for (Object[] arr : customers) {
				customerList.add(nativeQueryToCustomer(arr));
			}
			
			for(Customer customer: customerList) {
				if(customer.getId()==customerIdValue) {
					isOK = true;
					break;
				}
			}
		} else {
			Customer customer = customerRepository.findByIdAndOwnerId(customerIdValue, currentUserId);
			isOK = customer != null;
		}
		
		return new IdentityAvailability(isOK);
	}
}