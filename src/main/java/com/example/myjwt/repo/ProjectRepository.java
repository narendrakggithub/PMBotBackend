package com.example.myjwt.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Project;
import com.example.myjwt.models.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	Optional<Project> findByProjectName(String projectName);
	
	Boolean existsByProjectName(String projectName);
	
	Boolean existsByProjectNameAndCustomerId(String projectName, Long customerId);
	
	Boolean existsByProjectNameAndSubLobId(String projectName, Long subLobId);

	
}
