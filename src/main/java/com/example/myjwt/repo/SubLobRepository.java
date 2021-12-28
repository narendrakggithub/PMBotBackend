package com.example.myjwt.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Project;
import com.example.myjwt.models.SubLob;
import com.example.myjwt.models.User;

@Repository
public interface SubLobRepository extends JpaRepository<SubLob, Long> {
	Optional<SubLob> findBySubLobName(String subLobName);
	
	Boolean existsBySubLobName(String subLobName);

	Boolean existsBySubLobNameAndLobId(String subLobName, Long lobId);
}
