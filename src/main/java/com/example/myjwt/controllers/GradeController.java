package com.example.myjwt.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myjwt.models.Grade;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;
import com.example.myjwt.payload.IdentityAvailability;
import com.example.myjwt.payload.ListResponse;
import com.example.myjwt.payload.NativeQueryUser;
import com.example.myjwt.payload.UserIdentityAvailability;
import com.example.myjwt.payload.UserListItem;
import com.example.myjwt.payload.request.CreateSbuRequest;

import com.example.myjwt.payload.response.ApiResponse;
import com.example.myjwt.repo.GradeRepository;
import com.example.myjwt.repo.SbuRepository;
import com.example.myjwt.repo.UserRepository;
import com.example.myjwt.security.services.SbuService;
import com.example.myjwt.security.services.UserPrincipal;
import com.example.myjwt.security.services.UserService;
import com.example.myjwt.util.PMUtils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/grade")
public class GradeController extends BaseController {

	@Autowired
	private GradeRepository gradeRepository;

	private static final Logger logger = LoggerFactory.getLogger(GradeController.class);

	@GetMapping("/getAllGrades")
	public List<Grade> getAllGrades() {
		List<Grade> grades = gradeRepository.findAll();
		
		return grades;
	}

}