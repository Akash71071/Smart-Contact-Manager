package com.cm.controllers;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cm.dao.ContactRepository;
import com.cm.dao.UserRepository;
import com.cm.entities.Contact;
import com.cm.entities.User;

@RestController
public class SearchController 
{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
   //search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> serach(@PathVariable("query") String query,Principal principal)
	{
		System.out.println(query);
		//current user
		User user = this.userRepository.getUserByUserName(principal.getName());
		List<Contact> searchcontactlist = this.contactRepository.findByNameContainingAndUser(query,user);
		return ResponseEntity.ok(searchcontactlist);
	}
}
