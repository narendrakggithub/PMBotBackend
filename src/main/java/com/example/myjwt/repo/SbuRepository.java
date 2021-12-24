package com.example.myjwt.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Project;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;

@Repository
public interface SbuRepository extends JpaRepository<Sbu, Long> {
	Optional<Sbu> findBySbuName(String sbuName);
	
	Boolean existsBySbuName(String sbuName);

	
}
