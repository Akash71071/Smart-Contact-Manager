package com.cm.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cm.entities.Contact;
import com.cm.entities.User;

public interface ContactRepository  extends JpaRepository<Contact,Integer>
{
	@Query("from Contact as c where c.user.Id =:userId")
    public Page<Contact>  findContactsUsingById(@Param("userId") int userId,Pageable pageable);
	
	
	//search list
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
