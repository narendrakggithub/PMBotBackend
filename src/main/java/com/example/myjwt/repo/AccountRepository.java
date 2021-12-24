package com.example.myjwt.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Account;
import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Project;
import com.example.myjwt.models.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByAccountName(String accountName);
	
	Boolean existsByAccountName(String accountName);

	
}