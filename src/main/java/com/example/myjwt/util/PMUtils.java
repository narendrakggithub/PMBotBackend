package com.example.myjwt.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import com.example.myjwt.models.enm.EGrade;
import com.example.myjwt.security.jwt.JwtAuthenticationFilter;
import com.example.myjwt.security.jwt.JwtTokenProvider;
import com.example.myjwt.security.services.UserPrincipal;
import com.example.myjwt.security.services.CustomUserDetailsService;

public class PMUtils {

	private static final Logger logger = LoggerFactory.getLogger(PMUtils.class);

	public static String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

	public static Long getUserIdFromRequest(HttpServletRequest request, JwtTokenProvider jwtUtils,
			CustomUserDetailsService userDetailsService) {
		try {
			String jwt = PMUtils.parseJwt(request);
			if (jwt != null && jwtUtils.validateToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(username);
				return userDetails.getId();
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);

		}
		return null;
	}

	public static List<Long> getSBUHeadEligibleGrades() {
		List<Long> eligibleGrades = new ArrayList<Long>();

		eligibleGrades.add(Long.valueOf(EGrade.SVP.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.VP.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.AVP.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.SD.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.D.ordinal() + 1));

		return eligibleGrades;
	}

	public static List<Long> getPDLEligibleGrades() {
		List<Long> eligibleGrades = new ArrayList<Long>();
		eligibleGrades.add(Long.valueOf(EGrade.AVP.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.SD.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.D.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.AD.ordinal() + 1));

		return eligibleGrades;
	}

	public static List<Long> getEDLEligibleGrades() {
		List<Long> eligibleGrades = new ArrayList<Long>();

		eligibleGrades.add(Long.valueOf(EGrade.D.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.AD.ordinal() + 1));
		eligibleGrades.add(Long.valueOf(EGrade.SM.ordinal() + 1));

		return eligibleGrades;
	}
}
