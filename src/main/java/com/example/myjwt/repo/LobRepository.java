package com.example.myjwt.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Lob;
import com.example.myjwt.models.Project;
import com.example.myjwt.models.User;

@Repository
public interface LobRepository extends JpaRepository<Lob, Long> {
	Optional<Lob> findByLobName(String lobName);
	
	Boolean existsByLobNameAndAccountId(String lobName, Long accountId);

	
}
