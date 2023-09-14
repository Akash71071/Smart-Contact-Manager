package com.cm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cm.dao.UserRepository;
import com.cm.entities.User;

public class UserDetailsServiceImple  implements UserDetailsService
{
   @Autowired
   private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//fetching user from database
		User user = this.userRepository.getUserByUserName(username);
		
		if(user == null)
		{
			throw new UsernameNotFoundException("Could not found user !!");
		}
		
		CustomUserDeatails customUserDeatails = new CustomUserDeatails(user);
		
		return customUserDeatails;
	}
	

}
