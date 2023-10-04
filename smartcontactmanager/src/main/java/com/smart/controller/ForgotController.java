package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;

	
	
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
		
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session)
	{
		
		//generating otp of 4 digit
		
		Random random = new Random(1000);
		int otp = random.nextInt(10000);
		
		//write code for send otp to email
		
		
		boolean flag = this.emailService.sendOtpEmail(email, otp);
		
		
//		String subject="OTP from SCM";
//		String message=" OTP = "+otp;
//		String to=email;
//		
//		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag) {
			session.setAttribute("otp", otp);
			return "verify-otp";
		}
		else {
			
			
			session.setAttribute("message", "Check your email ID !!");
			return "forgot_email_form";
		}
		
		
		
	}

}
