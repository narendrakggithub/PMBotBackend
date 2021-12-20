package com.example.myjwt.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.myjwt.security.services.UserDetailsServiceImpl;
import com.example.myjwt.util.PMUtils;

public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("---------------AuthTokenFilter---------------------------------1");
		try {
			System.out.println("---------------AuthTokenFilter---------------------------------2");
			String jwt = PMUtils.parseJwt(request);
			System.out.println("---------------AuthTokenFilter---------------------------------3");
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				System.out.println("---------------AuthTokenFilter---------------------------------4");
				String username = jwtUtils.getUserNameFromJwtToken(jwt);
				System.out.println("---------------AuthTokenFilter---------------------------------5:"+username);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				System.out.println("---------------AuthTokenFilter---------------------------------6:"+userDetails);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				System.out.println("---------------AuthTokenFilter---------------------------------7:"+authentication);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				System.out.println("---------------AuthTokenFilter---------------------------------8:");

				SecurityContextHolder.getContext().setAuthentication(authentication);
				System.out.println("---------------AuthTokenFilter---------------------------------9:");
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	
}
