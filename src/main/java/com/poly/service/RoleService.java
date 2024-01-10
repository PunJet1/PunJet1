package com.poly.service;

import java.util.List;
import java.util.Optional;

import com.poly.entity.Role;

public interface RoleService {
	public List<Role> findAll();

	public Optional<Role> findById(String id);

}
