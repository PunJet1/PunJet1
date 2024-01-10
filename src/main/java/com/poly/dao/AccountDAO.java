package com.poly.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Account;

public interface AccountDAO extends JpaRepository<Account, String> {
	@Query("select DISTINCT ar.account from Authority ar where ar.role.id in ('STAF','DIRE')")
	List<Account> getAdministrators();

	@Query("SELECT a FROM Account a WHERE a.username =?1 and a.password=?2")
	Account getAccount(String username, String password);

	// Phuc vu viec gui mail
	@Query("SELECT a FROM Account a WHERE a.email=?1")
	public Account findByEmail(String email);

	@Query("SELECT a FROM Account a WHERE a.token=?1")
	public Account findByToken(String token);

//	@Query(value = "SELECT count(a.username) FROM Accounts a", nativeQuery = true)
//	Integer countAllAccount();
	Account findByUsername(String username);

}
