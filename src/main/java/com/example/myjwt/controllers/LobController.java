package com.example.myjwt.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.repo.LobRepository;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class LobController {

	@Autowired
	private LobRepository lobRepository;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);


	@GetMapping("/lob/checkLobNameAvailability")
	public IdentityAvailability checkLobNameAvailability(@RequestParam(value = "lobName") String lobName) {
		Boolean isAvailable = !lobRepository.existsByLobName(lobName);
		return new IdentityAvailability(isAvailable);
	}

}