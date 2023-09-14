package com.cm.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.swing.text.DefaultEditorKit.CopyAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cm.dao.ContactRepository;
import com.cm.dao.UserRepository;
import com.cm.entities.Contact;
import com.cm.entities.User;
import com.cm.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void CommanData(Model model, Principal principal) {
		// get username using principal security
		String username = principal.getName();
		System.out.println("UserName " + username);

		// fetch user data from database using username
		User user = this.userRepository.getUserByUserName(username);
		System.out.println(user);
		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String User_Dashboard() {

		System.out.println("User dashboard open");
		return "/normaluser/user_dashboard";
	}

	@GetMapping("/add-contact")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String AddContact(Model model) {
		model.addAttribute("contact", new Contact());
		return "/normaluser/add_contact";
	}

	// process-contact
	@Transactional
	@PostMapping("/process-contact")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String ProcessContact(@ModelAttribute("contact") Contact contact,
			@RequestParam("profileImage") MultipartFile file1, Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// process and upload Image
			if (file1.isEmpty()) {
				System.out.println("File is not avalable");
				contact.setImage("contact.jpg");
			} else {
				// set image name in contact
				contact.setImage(file1.getOriginalFilename());

				File savefile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file1.getOriginalFilename());

				Files.copy(file1.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is Uploaded");

			}

			// set user in contact
			contact.setUser(user);

			// add contact in user
			user.getContact().add(contact);

			// save user in database
			User user2 = this.userRepository.save(user);

			System.out.println("Contact " + contact);
			// return "normaluser/add_contact";
			session.setAttribute("message1", new Message("Contact Successfully Added !!", "alert-success"));
			return "normaluser/add_contact";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.setAttribute("message1", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
			return "normaluser/add_contact";
		}
	}

	// view contact handler
	// per page size =p[s]
	// current page
	@GetMapping("/view-contact/{page}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String ViewContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
//		List<Contact> contacts = user.getContact();
//		model.addAttribute("contact", contacts);

		// pageable
		Pageable pageable = PageRequest.of(page, 3);

		// for pagination
		Page<Contact> contacts = this.contactRepository.findContactsUsingById(user.getId(), pageable);

		// current page
		model.addAttribute("currentPage", page);

		// page of contacts
		model.addAttribute("contact", contacts);

		// total pages
		model.addAttribute("totalPages", contacts.getTotalPages());

		System.out.println("View Contact Open");
		return "normaluser/view_contact";
	}

	// Contact Detail
	@GetMapping("/contact-detail/{cid}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String ContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		try {
			// System.out.println("Contact Detail"+cid);
			Optional<Contact> contactoptional = this.contactRepository.findById(cid);

			Contact contact = contactoptional.get();

			// user
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);

			if (user.getId() == contact.getUser().getId()) {
				model.addAttribute("contact", contact);
			} else {
				return "normaluser/error";
			}

			return "normaluser/contact_detail";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "normaluser/error";
		}
	}

	// Delete Contact
	@Transactional
	@GetMapping("/delete-contact/{cid}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String DeleteContact(@PathVariable("cid") Integer cid, Principal principal, HttpSession session) {
		try {
			// delete
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);

			//
			Optional<Contact> optionalcontact = this.contactRepository.findById(cid);
			Contact contact = optionalcontact.get();

			if (user.getId() == contact.getUser().getId()) {
				// remove contact form list
				user.getContact().remove(contact);

				// again save user
				this.userRepository.save(user);

//				this.contactRepository.delete(contact);
				session.setAttribute("message1", new Message("Contact Deleted Successfully!!", "alert-success"));
				return "redirect:/user/view-contact/0";
			} else {
				return "normaluser/error";
			}
		} catch (Exception e) {
			// TODO: handle exception\
			e.printStackTrace();
			return "normaluser/error";
		}
	}

	// update contact
	@PostMapping("/update-contact/{cid}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String UpdateContact(@PathVariable("cid") Integer cid, Model model) {
		System.out.println("Update contact open");
		Contact contact = this.contactRepository.findById(cid).get();

		model.addAttribute("contact", contact);

		return "normaluser/update_contact";
	}

	// process update
	@PostMapping("/process-update")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String ProcessUpdate(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session, Model model) throws IOException {
		try {
			// old contact detail
			Contact oldcontact = this.contactRepository.findById(contact.getcId()).get();
			// image
			if (!file.isEmpty()) {
				// delete old file

				// save new file
				contact.setImage(file.getOriginalFilename());

				File savefile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is Uploaded");
			} else {
				contact.setImage(oldcontact.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			System.out.println("contact id" + contact.getcId());

			this.contactRepository.save(contact);
			session.setAttribute("message1", new Message("Contact Update Successfully!!", "alert-success"));
			return "redirect:/user/contact-detail/" + contact.getcId();
		} catch (Exception e) {
			// TODO: handle exception
			session.setAttribute("message1", new Message("Contact Not Update!!", "alert-danger"));
			model.addAttribute("contact", contact);
			return "normaluser/update_contact";
		}
	}

	// show profile
	@GetMapping("/user-profile")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String Profile() {
		System.out.println("profile open");
		return "normaluser/user_profile";
	}

	// setting handler
	@GetMapping("/setting")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String Setting() {

		return "normaluser/setting";
	}

	// password change form handler
	@GetMapping("/change-password")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String ChangePassword() {
		return "/normaluser/change_pass";
	}

	// process-password change form
	@PostMapping("/process-password-form")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String ProcessPasswordForm(@RequestParam("oldpassword") String oldpassword,
			@RequestParam("newpassword") String newpassword, Principal principal, HttpSession session) {
		System.out.println("Old pass: " + oldpassword);
		System.out.println("new pass: " + newpassword);

		// user old password get from db
		User user = this.userRepository.getUserByUserName(principal.getName());

		// check user input old and db old pass same or not
		if (bCryptPasswordEncoder.matches(oldpassword, user.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(newpassword));
			this.userRepository.save(user);
			session.setAttribute("message1", new Message("Password Change Successfully!!", "alert-success"));
		} else {
			session.setAttribute("message1", new Message("Old Password wrong!!", "alert-danger"));
		}
		return "/normaluser/change_pass";
	}

	// Delete User
	@GetMapping("/delete-user/{uid}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String DeleteContact(@PathVariable("uid") Integer uid) {
		try {
			this.userRepository.deleteById(uid);
			return "redirect:/logout";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:normaluser/user-profile";
		}
	}

	// update user
	@PostMapping("/update-user/{uid}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String updateUser(@PathVariable("uid") Integer uid, Model model) {
		Optional<User> optional = this.userRepository.findById(uid);
		User user = optional.get();
		model.addAttribute("userup", user);
		System.out.println("User selected" + user);
		return "normaluser/updateUser";
	}

	// Edit user
	@PostMapping("/updateUser/{Id}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String EditUser(@PathVariable Integer Id, @ModelAttribute("User") User user,
			@RequestParam("profileImage") MultipartFile file, Model model) throws IOException {
		
		User user2 = this.userRepository.findById(Id).get();
		user2.setId(user.getId());
		user2.setName(user.getName());
		user2.setEmail(user.getEmail());
		user2.setEnabled(user.isEnabled());
		user2.setAbout(user.getAbout());

		try {
			 if(!file.isEmpty())
			 {
				 user2.setImageUrl(file.getOriginalFilename());
				 
				    File savefile = new ClassPathResource("static/images").getFile();

					Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

					System.out.println("Image is Uploaded");
			 }
			 else
			 {
				 user2.setImageUrl(user2.getImageUrl());
			 }
			 System.out.println("Updated usere "+user2);
			 this.userRepository.save(user2);
			 return "redirect:/user/user-profile";
		} catch (Exception e) {
			// TODO: handle exception
			return "redirect:/user/user-profile";
		}
	}
}
