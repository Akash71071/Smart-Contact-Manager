package com.cm.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cm.dao.UserRepository;
import com.cm.entities.User;
import com.cm.helper.Message;
import com.cm.service.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class MainController 
{   
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	//Home
     @GetMapping("/home")
     public String Home()
     {
    	 return "home";
     }
     
     //About
     @GetMapping("/about")
     public String About()
     {
    	 return "about";
     }
     
     //verify Email
     @GetMapping("/signup")
     public String SignUp()
     {
    	 return "email_verify";
     }
     
     //send otp through email
     @PostMapping("/email-otp")
     public String EmailOtp(@RequestParam("email") String email,HttpSession session)
     {
    	 //random otp
    	 Random random = new Random();
    	 int  otp1 = random.nextInt(999999);
    	 System.out.println("Email :"+email);
    	 //prepair email
    	 //subject
    	 String subject="For Verify Email is Valid Or Not";
    	 // message
 		String message = "<div style='border:1px solid black;width:70%; text-shadow: 2px 2px 5px black; background:white; border:2px solid #701f9c;'>"
 				+ "<h2 style='text-align:center;'>Verify User Email</h2>" + "<h4 style='text-align:center; '><b>OTP: "
 				+ otp1 + "</b></h4>" + "</div>";
 		//to
    	 String to=email;
    	 
    	 //get user for dublicate email
    	 User user = this.userRepository.getUserByUserName(email);
    	 if(user == null)
    	 {
    		 try {
    				boolean sendMail = this.emailService.sendMail(message, subject,to);
    				if(sendMail) {
    					session.setAttribute("emailotp",otp1);
    					session.setAttribute("email",email);
    					session.setAttribute("message1",new Message("OTP sent on Email!!","alert-success"));
    					System.out.println("Email sent Successfully!!");
    					return "check_otp";
    				}
    				else
    				{
    					session.setAttribute("message1",new Message("Something Went Wrong!!","alert-danger"));
    					return "email_verify";
    				}
    			} catch (Exception e) {
    				// TODO: handle exception
    				e.printStackTrace();
    				return "email_verify";
    			}
    	 }
    	 else {
    		 session.setAttribute("message1",new Message("Email is alredy registered!!","alert-danger"));
    		 return "email_verify";
		}
    	  
     }
     
   //Signup
     @PostMapping("/signup-process")
     public String SignProcess(@RequestParam("otp") int otp,Model m,HttpSession session)
     {
    	 int emailotp = (int) session.getAttribute("emailotp");
    	 if (emailotp == otp) 
    	 {
    		 m.addAttribute("user",new User());
        	 return "signup";
		 }
    	 else
    	 {
    		 session.setAttribute("message1",new Message("OTP is Invalid!!","alert-danger"));
    		 return "check_otp";
    	 }
     }
     
   //Login
     @GetMapping("/signin")
     public String Login()
     {
    	 return "login";
     }
     
     //register
     @Transactional
     @PostMapping("/register")
     
     public String Register(@Valid @ModelAttribute("user") User user,BindingResult resulte1,@RequestParam("profileImage") MultipartFile file1,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,Model model,HttpSession session)
     {
    	 try {
    		 if(!agreement)
        	 {
    			 model.addAttribute("isNotAgreed",true);
        		 System.out.println("You have not agreed the terms and conditions");
        		 throw new Exception("You have not agreeed terms and conditions");
        	 }
    		 
    		 if(resulte1.hasErrors())
    		 {
    			 System.out.println("Error Occured");
    			 model.addAttribute("user",user);
    			 return "signup";
    		 }
        	 user.setRole("ROLE_USER");
        	 user.setEnabled(true);
        	 
        	// process and upload Image
 			if (file1.isEmpty()) {
 				System.out.println("File is not avalable");
 				user.setImageUrl("Defaulte.jpg");
 			} else {
 				// set image name in contact
 				user.setImageUrl(file1.getOriginalFilename());

 				File savefile = new ClassPathResource("static/images").getFile();

 				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file1.getOriginalFilename());

 				Files.copy(file1.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

 				System.out.println("Image is Uploaded");

 			}
        	 user.setPassword(passwordEncoder.encode(user.getPassword()));
        	 
        	 System.out.println("agreement="+agreement);
        	 System.out.println(user);
        	 
        	 User res = this.userRepository.save(user);
        	 
        	 model.addAttribute("user",new User());
        	 
        	 session.setAttribute("message1",new Message("Successfully Registered !!","alert-success"));
        	 return "signup";
        	 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message1",new Message("Something Went wrong !!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
     }
     
}
