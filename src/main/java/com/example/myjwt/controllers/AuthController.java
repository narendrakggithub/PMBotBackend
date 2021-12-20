package com.example.myjwt.controllers;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.example.myjwt.models.Role;
import com.example.myjwt.models.User;
import com.example.myjwt.models.enm.ERole;
import com.example.myjwt.payload.request.LoginRequest;
import com.example.myjwt.payload.request.SignupRequest;
import com.example.myjwt.payload.response.JwtResponse;
import com.example.myjwt.payload.response.MessageResponse;
import com.example.myjwt.repo.HexCodeRepository;
import com.example.myjwt.repo.RoleRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.jwt.JwtUtils;
import com.example.myjwt.security.services.UserDetailsImpl;
import com.example.myjwt.util.Constants;

import net.bytebuddy.utility.RandomString;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")

public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HexCodeRepository hexCodeRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		User user = userRepository.findByUserName(loginRequest.getUsername()).orElseThrow(
				() -> new UsernameNotFoundException("User Not Found with username: " + loginRequest.getUsername()));

		if (user.getIsVerified()) {
			if (user.getIsApproved()) {
				if (user.getIsActive()) {
					return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
							userDetails.getEmail(), roles));
				} else {
					return ResponseEntity.badRequest().body(new MessageResponse("Error: User not active"));
				}
			} else {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Approval pending with manager"));
			}
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Account not verified"));
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		if (userRepository.existsByUserName(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already exist!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already exist!"));
		}

		User user = new User();

		user.setIsVerified(false);
		user.setIsActive(false);
		user.setIsApproved(false);

		user.setUsername(signUpRequest.getUsername());
		user.setEmail(signUpRequest.getEmail());
		user.setPassword(encoder.encode(signUpRequest.getPassword()));

		/*
		 * Set<String> strRoles = signUpRequest.getRole(); Set<Role> roles = new
		 * HashSet<>();
		 * 
		 * if (strRoles == null) {
		 * 
		 * } else { strRoles.forEach(role -> { switch (role) { case "admin": Role
		 * adminRole = roleRepository.findByName(ERole.Admin) .orElseThrow(() -> new
		 * RuntimeException("Error: Role is not found.")); roles.add(adminRole);
		 * 
		 * break; default: Role userRole = roleRepository.findByName(ERole.Associate)
		 * .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		 * roles.add(userRole); } }); }
		 */

		Role userRole = roleRepository.findByName(ERole.Associate)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));

		user.setRole(userRole);

		System.out.println("signUpRequest.getManagerEmail()=" + signUpRequest.getManagerEmail());

		User manager = userRepository.findByEmail(signUpRequest.getManagerEmail());

		if (manager == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Manager email doesn't exist!"));
		}

		user.setManager(manager);

		// String siteURL = request.getRequestURL().toString();

		Hexcode hexCode = new Hexcode();
		hexCode.setTableName(Constants.TBL_USER);
		hexCode.setAction(Constants.HEXCODE_ACTION_VALIDATE);
		hexCode.setSubAction(Constants.HEXCODE_SUBACTION_EMAIL);
		String randomCode = RandomString.make(64);
		hexCode.setCode(randomCode);

		registerTransaction(user, hexCode);

		return ResponseEntity
				.ok(new MessageResponse("User registered successfully! Please verify the mail that has sent to you!!"));
	}

	@Transactional
	private void registerTransaction(User user, Hexcode hexCode)
			throws UnsupportedEncodingException, MessagingException {
		userRepository.save(user);
		hexCode.setRefId(user.getId());
		hexCodeRepository.save(hexCode);
		sendVerificationEmail(user, Constants.UI_URL, hexCode.getCode());
	}

	private void sendVerificationEmail(User user, String siteURL, String hexCode)
			throws MessagingException, UnsupportedEncodingException {
		String toAddress = user.getEmail();
		String fromAddress = "rapplicationdevelopment@gmail.com"; // ; password --> @DevTeam
		String senderName = "App Develop";
		String subject = "Please verify your registration";
		String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>" + "Thank you,<br>" + "App Dev Team";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", user.getUserName());
		String verifyURL = siteURL + "/verify?code=" + hexCode;

		content = content.replace("[[URL]]", verifyURL);

		helper.setText(content, true);

		mailSender.send(message);

	}
}
