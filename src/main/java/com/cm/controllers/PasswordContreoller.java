package com.cm.controllers;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cm.dao.UserRepository;
import com.cm.entities.User;
import com.cm.helper.Message;
import com.cm.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PasswordContreoller 
{
	 
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Forgot password
	@GetMapping("/forgot-password")
	public String ForgotPassword() {
		return "forgot_password";
	}

	// handel forgot password using otp
	@PostMapping("/forgot-form-handel")
	public String ForgotForm(@RequestParam("email") String email, HttpSession session) {
		// create otp
		// Random otp
		Random random = new Random();
		int otpp = random.nextInt(999999);
		System.out.println("otp : " + otpp);

		// prepair email
		// subject
		String subject = "OTP For Email Verify";
		// To
		String to = email;
		// message
		String message = "<div style='border:1px solid black;width:70%; text-shadow: 2px 2px 5px black; background:white; border:2px solid #701f9c;'>"
				+ "<h2 style='text-align:center;'>Verify User Email</h2>" + "<h4 style='text-align:center; '><b>OTP: "
				+ otpp + "</b></h4>" + "</div>";

		try {
			// get user
			User user = this.userRepository.getUserByUserName(email);
			if (user == null) {
				session.setAttribute("message1", new Message("Email is not valid !!", "alert-danger"));
				return "forgot_password";
			} else {
				// send email on otp page
				session.setAttribute("email", email);
				session.setAttribute("myotp", otpp);
				session.setAttribute("message1", new Message("Email is valid!!", "alert-success"));
				boolean mail = this.emailService.sendMail(message, subject, to);
				System.out.println("Mail is :" + mail);
				return "verify_otp";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "forgot_password";
		}
	}

	// verify otp is valid or not
	@PostMapping("/verify-otp")
	public String VerifyOTP(@RequestParam("otp") int otp, HttpSession session) {
		System.out.println("OTP :" + otp);
		System.out.println("myotp :" + session.getAttribute("myotp"));
		Integer myotp =(int)session.getAttribute("myotp");
		try {
			if (myotp==otp) 
			{
				session.setAttribute("message1",new Message("Email is verified","alert-success"));
				return "change_password";
			}
			else
			{
				session.setAttribute("message1",new Message("OTP is not valid!!","alert-danger"));
				return "verify_otp";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "verify_otp";
		}
	}

	//process newpass word
	@PostMapping("/process-newpass")
	public String ProcessPassword(@RequestParam("newpass") String newpass,HttpSession session)
	{
		try {
			//get user
			String email = (String) session.getAttribute("email");
			System.out.println("Email : "+email);
			User user = this.userRepository.getUserByUserName(email);
			user.setPassword(bCryptPasswordEncoder.encode(newpass));
			this.userRepository.save(user);
			System.out.println("Password changed successfully");
			session.setAttribute("message1",new Message("Password changed successfully!!","alert-success"));
			return "redirect:/signin";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}
}
