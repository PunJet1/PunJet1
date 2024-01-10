package com.poly.controller;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.dao.AccountDAO;
import com.poly.entity.Account;
import com.poly.service.AccountService;
import com.poly.service.MailerService;

import net.bytebuddy.utility.RandomString;

@Controller
public class AuthController {

	@Autowired
	AccountDAO accountDAO;

	@Autowired
	AccountService accountService;

	@Autowired
	MailerService mailer;

	@CrossOrigin("*")
	@ResponseBody
	@RequestMapping("/rest/auth/authentication")
	public Object getAuthentication(HttpSession session) {
		return session.getAttribute("authentication");
	}

	@RequestMapping("/auth/login/form")
	public String logInForm(Model model, @ModelAttribute("account") Account account) {
		List<String> roles = Arrays.asList("DIRE");

		// Thêm roles vào mô hình
		model.addAttribute("roles", roles);
		return "auth/login";
	}

	@RequestMapping("/auth/login/success")
	public String logInSuccess(Model model, @ModelAttribute("account") Account account) {
		model.addAttribute("message", "Đăng nhập thành công");
		return "redirect:/index";
	}

	@RequestMapping("/auth/login/error")
	public String logInError(Model model, @Validated @ModelAttribute("account") Account account, Errors errors) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Thông tin đăng nhập không chính xác!");
			return "auth/login";
		}
		return "auth/login";
	}

	@RequestMapping("/auth/unauthoried")
	public String unauthoried(Model model, @ModelAttribute("account") Account account) {
		model.addAttribute("message", "Bạn không có quyền truy cập!");
		return "auth/login";
	}

	@RequestMapping("/auth/logout/success")
	public String logOutSuccess(Model model, @ModelAttribute("account") Account account) {
		model.addAttribute("message", "Bạn đã đăng xuất!");
		return "auth/login";
	}

	// OAuth2
	@RequestMapping("/oauth2/login/success")
	public String oauth2(OAuth2AuthenticationToken oauth2) {
		accountService.loginFromOAuth2(oauth2);
		return "forward:/auth/login/success";
	}

	@GetMapping("/auth/register")
	public String signUpForm(Model model) {
		model.addAttribute("account", new Account());

		return "auth/register";
	}

	@PostMapping("/auth/register")
	public String signUpSuccess(Model model, @Validated @ModelAttribute("account") Account account, Errors error,
			HttpServletResponse response) {
		if (error.hasErrors()) {
			model.addAttribute("message", "Hãy sửa lỗi bên dưới!");
			return "auth/register";
		}
		account.setPhoto("user.png");
		account.setRoles("DIRE");
		account.setToken("token");
		accountService.create(account);
		model.addAttribute("message", "Đăng ký tài khoản mới thành công!");
		response.addHeader("refresh", "2;url=/auth/login/form");
		return "auth/register";
	}

	@GetMapping("/auth/forgot-password")
	public String forgotPasswordForm(Model model) {
		return "auth/forgot-password";
	}

	@PostMapping("/auth/forgot-password")
	public String processForgotPassword(@RequestParam("email") String email, HttpServletRequest request, Model model)
			throws Exception {
		try {
			String token = RandomString.make(50);
			accountService.updateToken(token, email);
			String resetLink = getSiteURL(request) + "/auth/reset-password?token=" + token;
			mailer.sendEmail(email, resetLink);
			model.addAttribute("message", "Chúng tôi đã gửi một liên kết đặt lại mật khẩu đến email của bạn. "
					+ "Nếu bạn không thấy email, hãy kiểm tra thư mục thư rác.");
		} catch (MessagingException e) {
			e.printStackTrace();
			model.addAttribute("error", "Lỗi khi gửi email");
		}
		return "auth/forgot-password";
	}

	@GetMapping("/auth/reset-password")
	public String resetPasswordForm(@Param(value = "token") String token, Model model) {
		Account account = accountService.getByToken(token);
		model.addAttribute("token", token);
		if (account == null) {
			model.addAttribute("message", "Invalid token!");
			return "redirect:/auth/login/form";
		}
		return "auth/reset-password";
	}

	@PostMapping("/auth/reset-password")
	public String processResetPassword(@RequestParam("token") String code, @RequestParam("password") String password,
			HttpServletResponse response, Model model) {
		Account token = accountService.getByToken(code);
		if (token == null) {
			model.addAttribute("message", "Invalid token!");
		} else {
			accountService.updatePassword(token, password);
			model.addAttribute("message", "Bạn đã thay đổi thành công mật khẩu của bạn!");
			response.addHeader("refresh", "2;url=/auth/login/form");
		}
		return "auth/reset-password";
	}

	@GetMapping("/auth/change-password")
	public String changePasswordForm(Model model) {
		return "auth/change-password";
	}

	@PostMapping("/auth/change-password")
	public String processChangePassword(Model model, @RequestParam("username") String username,
			@RequestParam("password") String newPassword) {
		Account account = accountService.findById(username);
		accountService.changePassword(account, newPassword);
		model.addAttribute("message", "Đổi mật khẩu thành công!");
		return "auth/change-password";
	}

	public String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}
}
