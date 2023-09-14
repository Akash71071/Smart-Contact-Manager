package com.cm.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper 
{	
	 public void removeMessageFromSession()
	   {
		 try {
			   System.out.println("Remove message");
			   HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			   session.removeAttribute("message1");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	   }

}
