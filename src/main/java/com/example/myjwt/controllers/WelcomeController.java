package com.example.myjwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.myjwt.models.Hexcode;
import com.example.myjwt.models.User;
import com.example.myjwt.repo.HexCodeRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.util.AppConstants;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/test")
public class WelcomeController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	HexCodeRepository hexCodeRepository;
	
	@GetMapping("/all")
	public String allAccess() {
		return "Welcome to the App. "
				+ "Let's Login or SignUp";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public String userAccess() {
		return "Hello user! You are authorized :) ";
	}
	
	@GetMapping("/verify/{vcode}")
	public String verifyUser(@PathVariable String vcode) {
		System.out.println(vcode);
		Hexcode hexCode = hexCodeRepository.findByCode(vcode);
		if (hexCode == null) {
			return "verify_failed! Verification invalid or already verified!";
		} else {

			switch (hexCode.getTableName()) {
			case AppConstants.TBL_USER:
				switch (hexCode.getAction()) {
				case AppConstants.HEXCODE_ACTION_VALIDATE:
					switch (hexCode.getSubAction()) {
					case AppConstants.HEXCODE_SUBACTION_EMAIL:

						User user = userRepository.findById(hexCode.getRefId())
								.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

						user.setIsVerified(true);
						updateUserAndDeleteHexCode(user, hexCode);

						return "verify_success!!!   Login to explore!!!";
					}
					break;
				}
				break;
			}

			return "Could not find relevant authentication !!!";
		}

	}
	
	@Transactional
	private void updateUserAndDeleteHexCode(User user, Hexcode hexCode) {
		userRepository.save(user);
		hexCodeRepository.delete(hexCode);
	}

}
