package com.cm.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "User")
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
     private int Id;
	 @NotBlank(message = "Name must not blank")
	 @Size(min = 2,max = 20,message = "2 to 20 characters allowed")
     private String Name;
     @Column(unique = true)
     @NotBlank(message = "Email must not blank")
     @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z]+\\.[A-Za-z]{2,4}$",message = " invalid Email !!Password must be in wall format")
     private String Email;
     @NotBlank(message = "Password must not blank")
//     @Pattern(regexp = "^[A-Za-z0-9]{4,8}$",message = "4 to 8 character allowed!!Password must be in wall format !!")
     private String Password;
     private String ImageUrl;
     private String Role;
     private boolean Enabled;
     @Column(length = 500)
     @NotBlank(message = "About must not blank")
     private String About;
     
     @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
     private List<Contact> contact;
     

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public User(int id, String name, String email, String password, String imageUrl, String role, boolean enabled,
			String about) {
		super();
		Id = id;
		Name = name;
		Email = email;
		this.Password = password;
		ImageUrl = imageUrl;
		Role = role;
		Enabled = enabled;
		About = about;
	}


	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		this.Password = password;
	}
	public String getImageUrl() {
		return ImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
	public String getRole() {
		return Role;
	}
	public void setRole(String role) {
		Role = role;
	}
	public boolean isEnabled() {
		return Enabled;
	}
	public void setEnabled(boolean enabled) {
		Enabled = enabled;
	}
	public String getAbout() {
		return About;
	}
	public void setAbout(String about) {
		About = about;
	}
	
	public List<Contact> getContact() {
		return contact;
	}

	public void setContact(List<Contact> contact) {
		this.contact = contact;
	}

	@Override
	public String toString() {
		return "User [Id=" + Id + ", Name=" + Name + ", Email=" + Email + ", Password=" + Password + ", ImageUrl="
				+ ImageUrl + ", Role=" + Role + ", Enabled=" + Enabled + ", About=" + About + ", contact=" + contact
				+ "]";
	}
     
}
