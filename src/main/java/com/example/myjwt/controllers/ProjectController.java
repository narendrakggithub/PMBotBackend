package com.example.myjwt.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.myjwt.exception.ResourceNotFoundException;
import com.example.myjwt.models.Account;
import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Project;
import com.example.myjwt.models.Role;
import com.example.myjwt.models.SubLob;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserProfile;
import com.example.myjwt.payload.UserSummary;
import com.example.myjwt.payload.request.CreateProjectRequest;
import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.CustomerRepository;
import com.example.myjwt.repo.ProjectRepository;
import com.example.myjwt.repo.SubLobRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.CurrentUser;
import com.example.myjwt.security.jwt.JwtTokenProvider;
import com.example.myjwt.security.services.CustomUserDetailsService;
import com.example.myjwt.security.services.RoleService;
import com.example.myjwt.security.services.UserPrincipal;
import com.example.myjwt.util.PMUtils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ProjectController extends BaseController {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private SubLobRepository subLobRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RoleService roleService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@GetMapping("/project/checkProjectNameAvailability")
	public IdentityAvailability checkProjectNameAvailability(@RequestParam(value = "projectName") String projectName) {
		Boolean isAvailable = !projectRepository.existsByProjectName(projectName);
		return new IdentityAvailability(isAvailable);
	}

	public SubLob nativeQueryToSubLob(Object[] arrValues) {
		SubLob subLob = new SubLob();
		int i = 0;
		subLob.setId(Long.parseLong(arrValues[i++].toString()));
		subLob.setSubLobName(arrValues[i++].toString());
		return subLob;
	}
	
	@PostMapping("/project/createproject")
	public ResponseEntity<?> createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		System.out.println("creating project");

		String projectName = createProjectRequest.getProjectName().trim();
		String pmUserName = createProjectRequest.getPmUserName().trim();
		Long currentUserId = getCurrentUserId();
		
		if(projectName.equals("")) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Project name cannot be blank!"));
		}
		
		if(pmUserName.equals("")) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: PM name cannot be blank!"));
		}
		
		User projectManager = userRepository.findByUserName(pmUserName)
				.orElseThrow(() -> new ResourceNotFoundException("PM User", "PM User", currentUserId));
		
		if(createProjectRequest.getStartDate().getTime()>=createProjectRequest.getEndDate().getTime()) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Start date cannot be later then or equal to end date!"));
		}
		
		if (projectRepository.existsByProjectNameAndCustomerId(projectName, createProjectRequest.getCustomerId())) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Error: Project name already exist for same customer!"));
		}
		
		User loggedInUser = userRepository.findById(currentUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user", currentUserId));
		
		Set<Role> roles = loggedInUser.getRoles();

		boolean isSubLobLead = false;

		for (Role role : roles) {
			if (role.getName().equals(ERole.SubLOBLead)) {
				isSubLobLead = true;
				break;
			}
		}
		
		if(createProjectRequest.getSubLobId()>0) {
			if (projectRepository.existsByProjectNameAndSubLobId(projectName, createProjectRequest.getSubLobId())) {
				return ResponseEntity.badRequest()
						.body(new ApiResponse(false, "Error: Project name already exist for same sub lob!"));
			}
		}
		
		SubLob subLob = null;
		Customer customer = null;
		
		if(isSubLobLead) {
			if(createProjectRequest.getSubLobId()<0)
				return ResponseEntity.badRequest()
						.body(new ApiResponse(false, "Error: Sub Lob ID missing!"));
			
			subLob = subLobRepository.findByIdAndOwnerId(createProjectRequest.getSubLobId(), currentUserId);
			if(subLob == null)
				return ResponseEntity.badRequest()
						.body(new ApiResponse(false, "Error: You are not the owner of this sublob!"));
			
			List<Object[]> customers = customerRepository.findCustomerInSameAccountOfLoggedInSubLobHead(currentUserId);
			List<Customer> customerList = new ArrayList<Customer>();
			for (Object[] arr : customers) {
				customerList.add(nativeQueryToCustomer(arr));
			}
			
			boolean isOK = false;
			for(Customer cust: customerList) {
				if(cust.getId()==createProjectRequest.getCustomerId()) {
					customer = cust;
					isOK = true;
					break;
				}
			}			
			if(!isOK)
				return ResponseEntity.badRequest()
						.body(new ApiResponse(false, "Error: Customer does not belong to same account!"));
		} else {
			if(createProjectRequest.getCustomerId()<0)
				return ResponseEntity.badRequest()
						.body(new ApiResponse(false, "Error: Sub Lob ID missing!"));
			
			customer = customerRepository.findByIdAndOwnerId(createProjectRequest.getCustomerId(), currentUserId);
			if(customer == null)
				return ResponseEntity.badRequest()
						.body(new ApiResponse(false, "Error: You are not the owner of this customer!"));
			
			if(createProjectRequest.getSubLobId()>0) {
				List<Object[]> subLobsArray = subLobRepository
						.findAllSubLobsInSameAccountOfLoggedInCustomerHead(currentUserId);
				
				List<SubLob> subLobList = new ArrayList<SubLob>();
				for (Object[] arr : subLobsArray) {
					subLobList.add(nativeQueryToSubLob(arr));
				}

				boolean isOK = false;
				for (SubLob itemSubLob : subLobList) {
					if (itemSubLob.getId() == createProjectRequest.getSubLobId()) {
						subLob = itemSubLob;
						isOK = true;
						break;
					}
				}
				
				if(!isOK)
					return ResponseEntity.badRequest()
							.body(new ApiResponse(false, "Error: Sub Lob does not belong to same account!"));
			}
			
		}

		Project project = new Project();
		project.setCustomer(customer);
		project.setEndDate(createProjectRequest.getEndDate());
		project.setIsActive(true);
		project.setProjectManager(projectManager);
		project.setProjectName(projectName);
		project.setStartDate(createProjectRequest.getStartDate());
		project.setSubLob(subLob);

		Set<Role> pmRoles = roleService.getAllRolesFor(ERole.ProjectManager);
		projectManager.setRoles(pmRoles);

		createProjectTransaction(projectManager, project);

		return ResponseEntity.ok(new ApiResponse(true, "Project added successfully!"));
	}
	
	public Customer nativeQueryToCustomer(Object[] arrValues) {

		Customer customer = new Customer();
		int i=0;
		customer.setId(Long.parseLong(arrValues[i++].toString()));
		customer.setCustomerName(arrValues[i++].toString());
		return customer;
		
	}

	@Transactional
	private Project createProjectTransaction(User user, Project project) {
		userRepository.save(user);
		Project result = projectRepository.save(project);
		return result;
	}
}