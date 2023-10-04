package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {

		String userName = principal.getName();
		System.out.println("Username " + userName);
		// get the user using username(Email)

		User user = userRepo.getUserByUserName(userName);
		System.out.println("User " + user);

		m.addAttribute("user", user);
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model m, Principal principal) {

		m.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	// open add form handler

	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);

			// processing and uploading file

			if (file.isEmpty()) {
				System.out.println("Empty Image");
				contact.setImage("contactlogo.png");
			} else {
				// file upload to folder and update name to contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is Uploaded");
			}

			user.getContacts().add(contact);
			contact.setUser(user);

			this.userRepo.save(user);

			System.out.println("DATA" + contact);
			System.out.println("Added to Database");
			// success message....

			session.setAttribute("message", new Message("Your contact is added !! Add more...", "success"));

		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());

			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something Went wrong!! Try Again..", "danger"));

		}

		return "normal/add_contact_form";
	}

	// show contact handler
	// per page=5[n]
	// current page=0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show User Contacts");

		// contact ki list ko bhejni h
		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);

		// currentPage-page
		// Contact per page-5
		PageRequest pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);

		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}

	// showing particular contact details...
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal principal) {
		m.addAttribute("title", "Contact Details");

		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();

		//checking
		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("title",contact.getName());
			m.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}
	
	//delete contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,Model m,Principal principal,HttpSession session) {
		
		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();
		
		
		
		//check...
		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			user.getContacts().remove(contact);
			this.userRepo.save(user); 
			session.setAttribute("message",new Message("Contact Deleted Successfully","success") );
		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	//open update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId,Model m) {
		
		m.addAttribute("title","Update Contact");
		
		Contact contact = this.contactRepo.findById(cId).get();
		
		m.addAttribute("contact",contact);
		
		return "normal/update_form";
	}
	
	//update
	@RequestMapping(value = "/process-update",method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
						Model m,HttpSession session,Principal principal) {
		
		try {
			//old contact details
			Contact oldcontactDetails = this.contactRepo.findById(contact.getcId()).get();
			
			//image..
			if(!file.isEmpty()) {
				//file work- rewrite
				//delete old pic 
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldcontactDetails.getImage());
				file1.delete();
				
				//update new pic
				
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}
			else {
				contact.setImage(oldcontactDetails.getImage());
			}
			User user=this.userRepo.getUserByUserName(principal.getName());
			
			contact.setUser(user);
			this.contactRepo.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated","success"));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	//your Profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title","Profile Page");
		return "normal/profile";
	}
	
	//settings
	@GetMapping("/setting")
	public String setting(Model m) {
		m.addAttribute("title", "Setting");

		return "normal/setting";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword
			,HttpSession session,Principal principal) {
		
		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);
		
		System.out.println(oldPassword);
		System.out.println(newPassword);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			//change the password
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepo.save(user);
			session.setAttribute("message", new Message("Password Changed Successfully!!","success"));
			
			}
		else {
			session.setAttribute("message", new Message("Wrong Old Password, Try Again!!","danger"));
			return "redirect:/user/setting";
		}
		
		return "redirect:/user/index";
	}

}
