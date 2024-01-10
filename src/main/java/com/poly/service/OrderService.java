package com.poly.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.entity.Order;

public interface OrderService {
	Order create(JsonNode orderData);

	Order findById(Long id);

	List<Order> findByUsername(String username);

	List<Order> findAll();

	Order update(Order order);

	void delete(Long id);

}
