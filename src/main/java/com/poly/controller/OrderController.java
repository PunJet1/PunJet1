package com.poly.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.service.OrderService;

@Controller
public class OrderController {

	@Autowired
	OrderService orderService;

	@Autowired
	HttpServletRequest request;

	@RequestMapping("/cart/view")
	public String cart() {
		return "cart/view";
	}

//	@RequestMapping("/cart/checkout")
//	public String checkout() {
//		if (!(request.isUserInRole("STAF") || request.isUserInRole("DIRE"))) {
//			return "redirect:/auth/login/form";
//		}
//		return "cart/checkout";
//	}

	@RequestMapping("/cart/checkout")
	public String checkout(HttpServletRequest request) {
		if (request.isUserInRole("STAF")) {
			// Tai khoan STAF không được phép mua hàng
			// Chuyển hướng hoặc xử lý phù hợp
			return "redirect:/auth/login/form";
		}
		// Other users are allowed to access the checkout page
		return "cart/checkout";
	}

	@RequestMapping("/order/list")
	public String list(Model model, HttpServletRequest request) {
		String username = request.getRemoteUser();
		model.addAttribute("orders", orderService.findByUsername(username));
		return "order/list";
	}

	@RequestMapping("/order/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		model.addAttribute("order", orderService.findById(id));
		return "order/detail";
	}

}
