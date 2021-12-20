package com.example.myjwt.controllers;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.myjwt.models.Hexcode;
import com.example.myjwt.models.Project;
import com.example.myjwt.models.Role;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
import com.example.myjwt.payload.request.CreateProjectRequest;
import com.example.myjwt.payload.request.LoginRequest;
import com.example.myjwt.payload.request.SignupRequest;
import com.example.myjwt.payload.response.JwtResponse;
import com.example.myjwt.payload.response.MessageResponse;
import com.example.myjwt.repo.HexCodeRepository;
import com.example.myjwt.repo.ProjectRepository;
import com.example.myjwt.repo.RoleRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.jwt.JwtUtils;
import com.example.myjwt.security.services.UserDetailsImpl;
import com.example.myjwt.security.services.UserDetailsServiceImpl;
import com.example.myjwt.util.Constants;
import com.example.myjwt.util.PMUtils;

import net.bytebuddy.utility.RandomString;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/form")

public class FormController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HexCodeRepository hexCodeRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/createproject")
	public ResponseEntity<?> createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {

		Long userId = PMUtils.getUserIdFromRequest(request, jwtUtils, userDetailsService);
		
		System.out.println("userId--------------------------------------->:"+userId);

		if (userId==null) {
			return ResponseEntity.badRequest().body(new MessageResponse("User doesn't exist"));
		}

		Project project = new Project();
		
		
		User user = userRepository.findById(userId).orElseThrow(
				() -> new UsernameNotFoundException("User Not Found with userid: " + userId));
		
		User projectManager = userRepository.findByUserName(createProjectRequest.getPmName()).orElseThrow(
				() -> new UsernameNotFoundException("Project Manager Not Found with name: " + createProjectRequest.getPmName()));
		
		project.setCreatedBy(user);
		project.setCustomer(null);
		project.setEndDate(createProjectRequest.getEndDate());
		project.setIsActive(true);
		project.setProjectManager(projectManager);
		project.setProjectName(createProjectRequest.getProjectName());
		project.setStartDate(createProjectRequest.getStartDate());
		project.setSubLob(null);
		
		projectRepository.save(project);

		return ResponseEntity
				.ok(new MessageResponse("Project added successfully!"));
	}
}
