package com.productmanagement.security.config;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.productmanagement.model.User;
import com.productmanagement.repo.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
	@Autowired
	private UserRepo userRepo;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			Optional<User> optUser = userRepo.findByEmail(username);
			if(optUser.isEmpty()) {
				log.error("Unable to find user [{}] trying to logging",username);
				throw new UsernameNotFoundException("Username is not valid ["+username+"]");
			}
			return optUser.get();
		}
		catch (Exception e) {
			System.err.println(e);
			throw new UsernameNotFoundException("Username is not valid ["+username+"]");
		}
	}


}
