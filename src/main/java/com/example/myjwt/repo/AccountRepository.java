package com.example.myjwt.repo;

import java.util.List;
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
	
	Optional<Account> findById(Long id);
	
	Optional<Account> findByIdAndEdlId(Long id, Long edlId);
	
	Optional<Account> findByAccountName(String accountName);
	
	Boolean existsByAccountName(String accountName);
	
	List<Account> findByEdlId(Long edlId);
	
	List<Account> findByPdlIdAndEdlIdAndIsActive(Long pdlId, Long edlId, Boolean isActive);
	
	Boolean existsByIdAndEdlId(Long id, Long edlId);
	
	Account findByAccountNameAndPdlIdAndIsActive(String accountName, Long pdlId, Boolean isActive);

	
}
