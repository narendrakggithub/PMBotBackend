package com.example.myjwt.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Grade;
import com.example.myjwt.models.Project;
import com.example.myjwt.models.SubLob;
import com.example.myjwt.models.User;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByCustomerName(String customerName);
	
	Boolean existsByCustomerName(String customerName);
	
	Boolean existsByCustomerNameAndAccountId(String customerName, Long accountId);

	Customer findByIdAndOwnerId(Long id, Long ownerId);
	
	List<Customer> findByOwnerId(Long ownerId);
	
	List<Customer> findAll();
	
	@Query(value = "SELECT id, customer_name FROM customer WHERE account_id=(SELECT account_id FROM lob WHERE id = (SELECT lob_id FROM sublob WHERE owner_id=:ownerId))", nativeQuery = true)
	List<Object[]> findCustomerInSameAccountOfLoggedInSubLobHead(Long ownerId);
}
