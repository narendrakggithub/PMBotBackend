package com.example.myjwt.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import com.example.myjwt.security.jwt.AuthTokenFilter;
import com.example.myjwt.security.jwt.JwtUtils;
import com.example.myjwt.security.services.UserDetailsImpl;
import com.example.myjwt.security.services.UserDetailsServiceImpl;

public class PMUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(PMUtils.class);
	
	public static String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

	public static Long getUserIdFromRequest(HttpServletRequest request, JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
		try {
			String jwt = PMUtils.parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetailsImpl userDetails = (UserDetailsImpl)userDetailsService.loadUserByUsername(username);
				return userDetails.getId();
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
			
		}
		return null;
	}
}
