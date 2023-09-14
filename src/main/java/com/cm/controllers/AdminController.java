package com.cm.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cm.dao.ContactRepository;
import com.cm.dao.UserRepository;
import com.cm.entities.User;
import com.cm.helper.Message;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/admin")
public class AdminController {
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
		
		List<User> users = this.userRepository.findAll();

		// not view admin
		List<User> list = new ArrayList<>();
		users.forEach(u -> {
			if (!u.getRole().contains("ROLE_ADMIN")) {
				list.add(u);
			}
		});
		model.addAttribute("Allusers", list);
	}

	@GetMapping("/index")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String Home() {
		return "/admin/admin_dashbord";
	}

	// view all users
	@GetMapping("/view-users")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String viewUser(Model model, Principal principal) {
		List<User> users = this.userRepository.findAll();

		// not view admin
		List<User> list = new ArrayList<>();
		users.forEach(u -> {
			if (!u.getRole().contains("ROLE_ADMIN")) {
				list.add(u);
			}
		});
		System.out.println("Users" + list);
		model.addAttribute("Allusers", list);
		return "/admin/view-user";
	}

	// Contact Detail
	@GetMapping("/user-detail/{uid}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String ContactDetail(@PathVariable("uid") Integer uid, Model model) {
		try {
			Optional<User> optional = this.userRepository.findById(uid);
			User user = optional.get();
			model.addAttribute("user", user);
			return "admin/view-user-detail";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "admin/error";
		}
	}

	// Delete user
	@GetMapping("/delete-user/{uid}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String DeleteContact(@PathVariable("uid") Integer uid) {
		try {
			this.userRepository.deleteById(uid);
			return "redirect:/admin/view-users";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/admin/view-users";
		}
	}

	@PostMapping("/update-user/{uid}")
	public String updateUser(@PathVariable("uid") Integer uid, Model model) {
		Optional<User> optional = this.userRepository.findById(uid);
		User user = optional.get();
		model.addAttribute("userup", user);
		System.out.println("User selected" + user);
		return "admin/updateUser";
	}

	@PostMapping("/updateUser/{Id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String EditUser(@PathVariable Integer Id, @ModelAttribute("User") User user, Model model) {
		User user2 = this.userRepository.findById(Id).get();
		user2.setId(user.getId());
		user2.setName(user.getName());
		user2.setEnabled(user.isEnabled());
		user2.setAbout(user.getAbout());
		System.out.println("Updated usere " + user2);
		this.userRepository.save(user2);
		return "redirect:/admin/view-users";
	}

	// show profile
	@GetMapping("/admin-profile")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String Profile() {
		System.out.println("profile open");
		return "admin/admin_profile";
	}

	// setting handler
	@GetMapping("/admin-setting")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String Setting() {
		return "admin/settingadmin";
	}

	// password change form handler
	@GetMapping("/change-password")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String ChangePassword() {
		return "admin/admin_change_pass";
	}

	// process-password change form
	@PostMapping("/process-password-form")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
		return "/admin/admin_change_pass";
	}

}
